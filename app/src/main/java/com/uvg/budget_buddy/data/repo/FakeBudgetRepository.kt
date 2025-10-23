package com.uvg.budget_buddy.data.repo

import com.uvg.budget_buddy.data.model.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

class FakeBudgetRepository : BudgetRepository {
    private val idGen = AtomicLong(1)
    private val _txs = MutableStateFlow<List<Transaction>>(emptyList())
    private val _simulateErrors = MutableStateFlow(false)

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun setSimulateErrors(enabled: Boolean) {
        _simulateErrors.value = enabled
    }

    override fun observeTransactions(): Flow<Resource<List<Transaction>>> =
        _txs.flatMapLatest { list ->
            flow {
                emit(Resource.Loading)
                delay(800)
                if (_simulateErrors.value && Random.nextInt(0, 4) == 0) {
                    emit(Resource.Error("Error simulado al obtener datos"))
                } else {
                    emit(Resource.Success(list))
                }
            }
        }.distinctUntilChanged()

    override suspend fun addIncome(amount: Double, description: String): Result<Unit> {
        delay(400)
        if (_simulateErrors.value && Random.nextBoolean()) {
            return Result.failure(Exception("Error simulado al agregar ingreso"))
        }
        val tx = Transaction(
            id = idGen.getAndIncrement(),
            description = if (description.isBlank()) "Ingreso" else description,
            amount = amount,
            dateText = today()
        )
        _txs.value = listOf(tx) + _txs.value
        return Result.success(Unit)
    }

    override suspend fun addExpense(amount: Double, description: String): Result<Unit> {
        delay(400)
        if (_simulateErrors.value && Random.nextBoolean()) {
            return Result.failure(Exception("Error simulado al agregar gasto"))
        }
        val tx = Transaction(
            id = idGen.getAndIncrement(),
            description = if (description.isBlank()) "Gasto" else description,
            amount = -amount,
            dateText = today()
        )
        _txs.value = listOf(tx) + _txs.value
        return Result.success(Unit)
    }
}
