package com.example.bankingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.repository.BankingRepository

class BankingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BankingRepository()

    val user: LiveData<User> = repository.getUser
    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions
    val transactionsSortedByAmount: LiveData<List<Transaction>> = repository.transactionsSortedByAmount

    fun insertUser(user: User) {
        repository.insertUser(user)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun insertTransaction(transaction: Transaction) {
        repository.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        repository.deleteTransaction(transaction)
    }

    fun searchTransactions(query: String): LiveData<List<Transaction>> {
        return repository.searchTransactions(query)
    }

    fun withdrawMoney(user: User, amount: Double) {
        val newBalance = user.balance - amount
        val updatedUser = user.copy(balance = newBalance)
        val transaction = Transaction(
            type = "Withdraw",
            amount = amount,
            timestamp = System.currentTimeMillis(),
            recipientInfo = "Self"
        )
        repository.performWithdrawal(updatedUser, transaction)
    }

    fun transferMoney(user: User, amount: Double, recipient: String, note: String) {
        val newBalance = user.balance - amount
        val updatedUser = user.copy(balance = newBalance)
        val recipientInfoComplete = if (note.isNotBlank()) "$recipient ($note)" else recipient
        val transaction = Transaction(
            type = "Transfer",
            amount = amount,
            timestamp = System.currentTimeMillis(),
            recipientInfo = recipientInfoComplete
        )
        repository.performWithdrawal(updatedUser, transaction)
    }
}
