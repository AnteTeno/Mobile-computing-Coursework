package com.example.coursework.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entry")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val grams: Double,
    val date: String
)
