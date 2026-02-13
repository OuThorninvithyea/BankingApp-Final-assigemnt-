package com.example.bankingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String, // "Deposit", "Withdraw", "Transfer"
    val amount: Double,
    val timestamp: Long,
    val recipientInfo: String? = null // Nullable for Deposit/Withdraw
) : Serializable
