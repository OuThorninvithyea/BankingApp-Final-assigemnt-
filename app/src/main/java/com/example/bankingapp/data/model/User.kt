package com.example.bankingapp.data.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class User(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var balance: Double = 0.0,
    var accountNumber: String = "",
    var profileImageUri: String? = null
) : Serializable
