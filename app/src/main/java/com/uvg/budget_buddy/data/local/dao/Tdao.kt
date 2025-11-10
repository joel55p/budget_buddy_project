package com.uvg.budget_buddy.data.local.dao

import androidx.room.*
import com.uvg.budget_buddy.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY dateText DESC")
    fun observeTransactionsByUser(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId")
    suspend fun getTransactionsByUser(userId: String): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)

    @Query("SELECT * FROM transactions WHERE syncedWithFirebase = 0 AND userId = :userId")
    suspend fun getUnsyncedTransactions(userId: String): List<TransactionEntity>

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
}