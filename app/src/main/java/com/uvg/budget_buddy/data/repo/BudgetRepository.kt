package com.uvg.budget_buddy.data.repo

import com.uvg.budget_buddy.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeTransactions(): Flow<Resource<List<Transaction>>>
    suspend fun addIncome(amount: Double, description: String): Result<Unit>
    suspend fun addExpense(amount: Double, description: String): Result<Unit>
    fun setSimulateErrors(enabled: Boolean)
}
