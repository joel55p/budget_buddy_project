package com.uvg.budget_buddy.data.model

data class Transaction(
    val id: Long,
    val description: String,
    val amount: Double,
    val dateText: String
)
