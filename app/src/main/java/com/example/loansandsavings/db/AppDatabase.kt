package com.example.loansandsavings.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Loan::class, Saving::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun loansDao(): LoansDao
    abstract fun savingDao(): SavingsDao
}
