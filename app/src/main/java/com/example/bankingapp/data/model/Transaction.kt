package com.example.bankingapp.data.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Transaction(
    @DocumentId
    var id: String = "",
    var type: String = "", // "Deposit", "Withdraw", "Transfer"
    var amount: Double = 0.0,
    var timestamp: Long = 0L,
    var recipientInfo: String? = null
) : Serializable
