package com.example.bankingapp.data.repository

import androidx.lifecycle.LiveData
import com.example.bankingapp.data.local.BankingDao
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.data.model.User

import androidx.room.withTransaction

class BankingRepository(private val bankingDao: BankingDao) {

    val getUser: LiveData<User> = bankingDao.getUser()
    val allTransactions: LiveData<List<Transaction>> = bankingDao.getAllTransactions()
    val transactionsSortedByAmount: LiveData<List<Transaction>> = bankingDao.getTransactionsSortedByAmount()

    // --- User Operations ---
    suspend fun insertUser(user: User) {
        bankingDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        bankingDao.updateUser(user)
    }

    // --- Transaction Operations ---
    suspend fun insertTransaction(transaction: Transaction) {
        bankingDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        bankingDao.deleteTransaction(transaction)
    }

    fun searchTransactions(query: String): LiveData<List<Transaction>> {
        return bankingDao.searchTransactions("%$query%")
    }

    suspend fun performWithdrawal(user: User, transaction: Transaction, database: com.example.bankingapp.data.local.BankingDatabase) {
        database.withTransaction {
            bankingDao.updateUser(user)
            bankingDao.insertTransaction(transaction)
        }
    }
}
