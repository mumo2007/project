package com.example.loansandsavings.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {
    @Insert
    suspend fun insertSaving(saving: Saving)

    @Query("SELECT * FROM savings ORDER BY id DESC")
    suspend fun getAllSavings(): List<Saving>

}