package com.example.bankingapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bankingapp.data.model.Transaction
import com.example.bankingapp.data.model.User

@Database(entities = [User::class, Transaction::class], version = 1, exportSchema = false)
abstract class BankingDatabase : RoomDatabase() {

    abstract fun bankingDao(): BankingDao

    companion object {
        @Volatile
        private var INSTANCE: BankingDatabase? = null

        fun getDatabase(context: Context): BankingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BankingDatabase::class.java,
                    "banking_database"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
