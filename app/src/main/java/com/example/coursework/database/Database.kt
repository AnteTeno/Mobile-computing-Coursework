package com.example.coursework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coursework.database.dao.FoodEntryDao
import com.example.coursework.database.dao.UserDao
import com.example.coursework.database.entities.FoodEntry
import com.example.coursework.database.entities.User

@Database(entities = [User::class, FoodEntry::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodEntryDao(): FoodEntryDao
}