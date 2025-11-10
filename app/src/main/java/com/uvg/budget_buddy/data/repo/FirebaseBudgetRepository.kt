package com.uvg.budget_buddy.data.repo

import com.google.firebase.database.*
import com.uvg.budget_buddy.data.local.dao.TransactionDao
import com.uvg.budget_buddy.data.local.entity.TransactionEntity
import com.uvg.budget_buddy.data.model.Transaction
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirebaseBudgetRepository(
    private val database: FirebaseDatabase,
    private val transactionDao: TransactionDao,
    private val authRepository: AuthRepository
) : BudgetRepository {

    private val _simulateErrors = MutableStateFlow(false)
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // NUEVO: StateFlow para forzar recargas
    private val _refreshTrigger = MutableStateFlow(0L)

    private fun getTransactionsRef(userId: String): DatabaseReference {
        return database.reference.child("transactions").child(userId)
    }

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun setSimulateErrors(enabled: Boolean) {
        _simulateErrors.value = enabled
    }

    override fun observeTransactions(): Flow<Resource<List<Transaction>>> {
        val userId = authRepository.currentUser?.uid
            ?: return flowOf(Resource.Error("Usuario no autenticado"))

        // SOLUCIÓN: Combinar Room local + Firebase + trigger manual
        return combine(
            transactionDao.observeTransactionsByUser(userId),
            _refreshTrigger,
            observeFirebaseTransactions(userId)
        ) { localTransactions, _, firebaseResource ->
            when (firebaseResource) {
                is Resource.Loading -> {
                    // Si hay datos locales, mostrarlos mientras carga
                    if (localTransactions.isNotEmpty()) {
                        Resource.Success(localTransactions.map { it.toTransaction() })
                    } else {
                        Resource.Loading
                    }
                }
                is Resource.Success -> {
                    // Siempre priorizar Firebase cuando esté disponible
                    firebaseResource
                }
                is Resource.Error -> {
                    // Fallback a datos locales si Firebase falla
                    if (localTransactions.isNotEmpty()) {
                        Resource.Success(localTransactions.map { it.toTransaction() })
                    } else {
                        firebaseResource
                    }
                }
            }
        }
    }

    private fun observeFirebaseTransactions(userId: String): Flow<Resource<List<Transaction>>> {
        return callbackFlow {
            trySend(Resource.Loading)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    repositoryScope.launch {
                        try {
                            val transactions = mutableListOf<Transaction>()
                            snapshot.children.forEach { child ->
                                child.getValue(TransactionFirebase::class.java)?.let { fb ->
                                    transactions.add(fb.toTransaction(child.key ?: ""))

                                    // Guardar en Room
                                    transactionDao.insertTransaction(
                                        fb.toTransactionEntity(child.key ?: "", userId)
                                    )
                                }
                            }
                            trySend(Resource.Success(transactions as List<Transaction>))
                        } catch (e: Exception) {
                            trySend(Resource.Error(e.message ?: "Error al procesar datos"))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Resource.Error(error.message))
                }
            }

            getTransactionsRef(userId).addValueEventListener(listener)

            awaitClose {
                getTransactionsRef(userId).removeEventListener(listener)
            }
        }
    }

    override suspend fun addIncome(amount: Double, description: String): Result<Unit> {
        val userId = authRepository.currentUser?.uid
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return withContext(Dispatchers.IO) {
            try {
                val id = getTransactionsRef(userId).push().key
                    ?: return@withContext Result.failure(Exception("Error al generar ID"))

                val transaction = TransactionFirebase(
                    description = if (description.isBlank()) "Ingreso" else description,
                    amount = amount,
                    dateText = today()
                )

                // 1. Guardar en Room primero
                val entity = transaction.toTransactionEntity(id, userId)
                transactionDao.insertTransaction(entity)

                // 2. NUEVO: Forzar refresh inmediato
                _refreshTrigger.value = System.currentTimeMillis()

                // 3. Sincronizar con Firebase en background
                try {
                    getTransactionsRef(userId).child(id).setValue(transaction).await()
                    transactionDao.updateTransaction(entity.copy(syncedWithFirebase = true))
                } catch (e: Exception) {
                    // Quedará marcado como no sincronizado
                    // pero ya se ve en la UI por Room
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun addExpense(amount: Double, description: String): Result<Unit> {
        val userId = authRepository.currentUser?.uid
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return withContext(Dispatchers.IO) {
            try {
                val id = getTransactionsRef(userId).push().key
                    ?: return@withContext Result.failure(Exception("Error al generar ID"))

                val transaction = TransactionFirebase(
                    description = if (description.isBlank()) "Gasto" else description,
                    amount = -amount,
                    dateText = today()
                )

                // 1. Guardar en Room primero
                val entity = transaction.toTransactionEntity(id, userId)
                transactionDao.insertTransaction(entity)

                // 2. NUEVO: Forzar refresh inmediato
                _refreshTrigger.value = System.currentTimeMillis()

                // 3. Sincronizar con Firebase en background
                try {
                    getTransactionsRef(userId).child(id).setValue(transaction).await()
                    transactionDao.updateTransaction(entity.copy(syncedWithFirebase = true))
                } catch (e: Exception) {
                    // Quedará marcado como no sincronizado
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncPendingTransactions() {
        val userId = authRepository.currentUser?.uid ?: return

        val unsyncedTransactions = transactionDao.getUnsyncedTransactions(userId)

        unsyncedTransactions.forEach { entity ->
            try {
                val transaction = TransactionFirebase(
                    description = entity.description,
                    amount = entity.amount,
                    dateText = entity.dateText
                )
                getTransactionsRef(userId).child(entity.id).setValue(transaction).await()
                transactionDao.updateTransaction(entity.copy(syncedWithFirebase = true))
            } catch (e: Exception) {
                // Reintentará en la próxima sincronización
            }
        }
    }
}

// Modelo para Firebase
data class TransactionFirebase(
    val description: String = "",
    val amount: Double = 0.0,
    val dateText: String = ""
) {
    fun toTransaction(id: String) = Transaction(
        id = id.hashCode().toLong(),
        description = description,
        amount = amount,
        dateText = dateText
    )

    fun toTransactionEntity(id: String, userId: String) = TransactionEntity(
        id = id,
        userId = userId,
        description = description,
        amount = amount,
        dateText = dateText,
        syncedWithFirebase = false
    )
}

// Extensión para convertir Entity a Transaction
fun TransactionEntity.toTransaction() = Transaction(
    id = id.hashCode().toLong(),
    description = description,
    amount = amount,
    dateText = dateText
)