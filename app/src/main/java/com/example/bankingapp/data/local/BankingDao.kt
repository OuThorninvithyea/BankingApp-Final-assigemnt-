package com.example.bankingapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.data.model.User

@Dao
interface BankingDao {

    // --- User Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): LiveData<User>

    // --- Transaction Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transaction_table ORDER BY timestamp DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE type LIKE :searchQuery OR recipientInfo LIKE :searchQuery ORDER BY timestamp DESC")
    fun searchTransactions(searchQuery: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transaction_table ORDER BY amount DESC")
    fun getTransactionsSortedByAmount(): LiveData<List<Transaction>>
}
