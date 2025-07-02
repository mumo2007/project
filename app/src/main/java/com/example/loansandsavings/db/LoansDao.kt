package com.example.loansandsavings.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query



@Dao
interface LoansDao {

    @Insert
    suspend fun save(loan: Loan)

    @Query("SELECT * FROM loans ORDER BY date DESC")
    suspend fun fetch(): List<Loan>

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoan(id: Int)

    @Query("UPDATE loans SET paid = :paid WHERE id = :id")
    suspend fun updateLoan(id: Int, paid:Boolean)

}