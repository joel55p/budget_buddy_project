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

        return callbackFlow {
            trySend(Resource.Loading)

            // Listener de Firebase
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
        }.onStart {
            // Cargar datos locales primero para mostrar algo mientras se conecta a Firebase
            try {
                val entities = transactionDao.getTransactionsByUser(userId)
                if (entities.isNotEmpty()) {
                    emit(Resource.Success(entities.map { it.toTransaction() }))
                }
            } catch (e: Exception) {
                // Si falla, continuará con Firebase
            }
        }.catch { e ->
            // Si falla Firebase, intentar cargar datos locales como fallback
            val entities = transactionDao.getTransactionsByUser(userId)
            if (entities.isNotEmpty()) {
                emit(Resource.Success(entities.map { it.toTransaction() }))
            } else {
                emit(Resource.Error("Error de conexión y sin datos locales: ${e.message}"))
            }
        }
    }

    override suspend fun addIncome(amount: Double, description: String): Result<Unit> {
        val userId = authRepository.currentUser?.uid
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val id = getTransactionsRef(userId).push().key
                ?: return Result.failure(Exception("Error al generar ID"))

            val transaction = TransactionFirebase(
                description = if (description.isBlank()) "Ingreso" else description,
                amount = amount,
                dateText = today()
            )

            // Guardar localmente primero
            val entity = transaction.toTransactionEntity(id, userId)
            transactionDao.insertTransaction(entity)

            // Sincronizar con Firebase
            try {
                getTransactionsRef(userId).child(id).setValue(transaction).await()
                // Marcar como sincronizado
                transactionDao.updateTransaction(entity.copy(syncedWithFirebase = true))
            } catch (e: Exception) {
                // Quedará marcado como no sincronizado para reintento posterior
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addExpense(amount: Double, description: String): Result<Unit> {
        val userId = authRepository.currentUser?.uid
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val id = getTransactionsRef(userId).push().key
                ?: return Result.failure(Exception("Error al generar ID"))

            val transaction = TransactionFirebase(
                description = if (description.isBlank()) "Gasto" else description,
                amount = -amount,
                dateText = today()
            )

            // Guardar localmente primero
            val entity = transaction.toTransactionEntity(id, userId)
            transactionDao.insertTransaction(entity)

            // Sincronizar con Firebase
            try {
                getTransactionsRef(userId).child(id).setValue(transaction).await()
                // Marcar como sincronizado
                transactionDao.updateTransaction(entity.copy(syncedWithFirebase = true))
            } catch (e: Exception) {
                // Quedará marcado como no sincronizado para reintento posterior
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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