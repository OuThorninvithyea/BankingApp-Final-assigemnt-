package com.example.bankingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bankingapp.data.local.BankingDatabase
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.repository.BankingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BankingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BankingRepository
    val user: LiveData<User>
    val allTransactions: LiveData<List<Transaction>>

    init {
        val database = BankingDatabase.getDatabase(application)
        repository = BankingRepository(database.bankingDao())
        user = repository.getUser
        allTransactions = repository.allTransactions
    }

    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateUser(user)
    }

    fun insertTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTransaction(transaction)
    }

    fun searchTransactions(query: String): LiveData<List<Transaction>> {
        return repository.searchTransactions(query)
    }

    val transactionsSortedByAmount: LiveData<List<Transaction>> = repository.transactionsSortedByAmount


    fun withdrawMoney(user: User, amount: Double) = viewModelScope.launch(Dispatchers.IO) {
        val newBalance = user.balance - amount
        val updatedUser = user.copy(balance = newBalance)
        val transaction = Transaction(
            type = "Withdraw",
            amount = amount,
            timestamp = System.currentTimeMillis(),
            recipientInfo = "Self"
        )
        val database = BankingDatabase.getDatabase(getApplication())
        repository.performWithdrawal(updatedUser, transaction, database)
    }

    fun transferMoney(user: User, amount: Double, recipient: String, note: String) = viewModelScope.launch(Dispatchers.IO) {
        val newBalance = user.balance - amount
        val updatedUser = user.copy(balance = newBalance)
        val recipientInfoComplete = if (note.isNotBlank()) "$recipient ($note)" else recipient
        val transaction = Transaction(
            type = "Transfer",
            amount = amount,
            timestamp = System.currentTimeMillis(),
            recipientInfo = recipientInfoComplete
        )
        val database = BankingDatabase.getDatabase(getApplication())
        repository.performWithdrawal(updatedUser, transaction, database)
    }
}
