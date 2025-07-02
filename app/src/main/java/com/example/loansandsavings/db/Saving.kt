package com.example.loansandsavings.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Savings")
data class Saving(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Float,
    val description: String,
    val date: Long

)