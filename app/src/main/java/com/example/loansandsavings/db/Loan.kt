package com.example.loansandsavings.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Loans")
data class Loan (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val names: String,
    val amount: Float,
    val date: Long,
    val paid: Boolean = false
)