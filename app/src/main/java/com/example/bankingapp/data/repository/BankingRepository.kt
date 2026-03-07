package com.example.bankingapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BankingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val transactionsCollection = db.collection("transactions")

    // Assuming a single user system for this app assignment, we use a fixed document ID
    private val currentUserId = "default_user_doc"

    private val _user = MutableLiveData<User>()
    val getUser: LiveData<User> = _user

    private val _allTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions

    private val _searchTransactions = MutableLiveData<List<Transaction>>()

    private val _transactionsSortedByAmount = MutableLiveData<List<Transaction>>()
    val transactionsSortedByAmount: LiveData<List<Transaction>> = _transactionsSortedByAmount

    init {
        // Listen to User updates in real-time
        usersCollection.document(currentUserId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("BankingRepository", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                _user.postValue(user!!)
            }
        }

        // Listen to Transactions updates in real-time (Sorted by Timestamp DESC)
        transactionsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("BankingRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val transactions = snapshot.toObjects(Transaction::class.java)
                    _allTransactions.postValue(transactions)
                }
            }

        // Listen to Transactions sorted by Amount DESC
        transactionsCollection.orderBy("amount", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _transactionsSortedByAmount.postValue(snapshot.toObjects(Transaction::class.java))
                }
            }
    }

    fun insertUser(user: User) {
        usersCollection.document(currentUserId).set(user)
    }

    fun updateUser(user: User) {
        usersCollection.document(currentUserId).set(user)
    }

    fun insertTransaction(transaction: Transaction) {
        val newDocRef = transactionsCollection.document()
        transaction.id = newDocRef.id
        newDocRef.set(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        transactionsCollection.document(transaction.id).delete()
    }

    fun searchTransactions(query: String): LiveData<List<Transaction>> {
        // In Firestore, substring search is complex. We will fetch all and filter locally for simplicity in this assignment.
        transactionsCollection.orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                val allTx = snapshot.toObjects(Transaction::class.java)
                val filtered = allTx.filter {
                    (it.type.contains(query, ignoreCase = true)) ||
                    (it.recipientInfo?.contains(query, ignoreCase = true) == true)
                }
                _searchTransactions.postValue(filtered)
            }
        return _searchTransactions
    }

    fun performWithdrawal(updatedUser: User, transaction: Transaction) {
        // Perform a batched write to ensure both complete or both fail
        db.runBatch { batch ->
            val userRef = usersCollection.document(currentUserId)
            batch.set(userRef, updatedUser)

            val newTxRef = transactionsCollection.document()
            transaction.id = newTxRef.id
            batch.set(newTxRef, transaction)
        }.addOnFailureListener { e ->
            Log.w("BankingRepository", "Error performing withdrawal/transfer", e)
        }
    }
}
