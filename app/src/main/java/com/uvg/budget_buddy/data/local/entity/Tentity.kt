package com.uvg.budget_buddy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val description: String,
    val amount: Double,
    val dateText: String,
    val syncedWithFirebase: Boolean = false
)