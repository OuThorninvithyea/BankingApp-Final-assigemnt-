# Firebase Database Migration Instructions for Claude

Hello! The user wants you to execute the migration from Room SQLite to Firebase Cloud Firestore. I have already analyzed their codebase in the `com.example.bankingapp` project and prepared the exact steps and code changes required.

Please execute the following steps precisely.

## 1. Verify Prerequisites
Ensure that the user has placed the `google-services.json` file inside the `app/` directory (`/Users/outhorninvuth/Documents/GitHub/BankingApp-Final-assigemnt-/app/google-services.json`).

## 2. Update `build.gradle` (Project Level)
Modify the project-level `build.gradle` (`/Users/outhorninvuth/Documents/GitHub/BankingApp-Final-assigemnt-/build.gradle`):
Add the Google Services classpath inside the `dependencies` block of `buildscript`:
```gradle
buildscript {
    dependencies {
        // ... existing dependencies
        classpath 'com.google.gms:google-services:4.4.2'
    }
}
```

## 3. Update `app/build.gradle` (App Level)
Modify the app-level `app/build.gradle` (`/Users/outhorninvuth/Documents/GitHub/BankingApp-Final-assigemnt-/app/build.gradle`):
1. Add the Google Services plugin at the top:
   ```gradle
   apply plugin: 'com.google.gms.google-services'
   ```
2. Remove all Room dependencies from the `dependencies` block. Remove `kapt 'androidx.room:room-compiler...'` completely (you can also remove `apply plugin: 'kotlin-kapt'` if nothing else uses it, but it's safe to leave).
3. Add Firebase dependencies:
   ```gradle
   implementation platform('com.google.firebase:firebase-bom:33.7.0')
   implementation 'com.google.firebase:firebase-firestore'
   ```

## 4. Add Internet Permission
Modify `app/src/main/AndroidManifest.xml`. Add this line before the `<application>` tag:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 5. Modify Data Models
Firestore requires data classes to have empty constructors. We achieve this by giving every parameter a default value. We also remove Room annotations.

Overwrite `app/src/main/java/com/example/bankingapp/data/model/User.kt`:
```kotlin
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
```

Overwrite `app/src/main/java/com/example/bankingapp/data/model/Transaction.kt`:
```kotlin
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
```

## 6. Delete Room Components
Execute commands to delete the old Room database and DAO:
* `rm app/src/main/java/com/example/bankingapp/data/local/BankingDao.kt`
* `rm app/src/main/java/com/example/bankingapp/data/local/BankingDatabase.kt` (or wherever the `RoomDatabase` class is located).

## 7. Overwrite `BankingRepository.kt`
Overwrite `app/src/main/java/com/example/bankingapp/data/repository/BankingRepository.kt` with the new Firebase implementation:

```kotlin
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
```

## 8. Overwrite `BankingViewModel.kt`
Overwrite `app/src/main/java/com/example/bankingapp/viewmodel/BankingViewModel.kt`:

```kotlin
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
```

## 9. Sync and Clean
Tell the user to do a Gradle Sync and run the app to make sure everything compiles and connects to Firebase.
