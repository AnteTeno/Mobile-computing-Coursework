package com.example.coursework.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.coursework.database.entities.FoodEntry

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM food_entry WHERE date = :date ORDER BY id DESC")
    fun getEntriesForDate(date: String): List<FoodEntry>

    @Insert
    fun insert(entry: FoodEntry)

    @Delete
    fun delete(entry: FoodEntry)
}
