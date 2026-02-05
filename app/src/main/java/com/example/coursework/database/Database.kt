package com.example.coursework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coursework.database.dao.UserDao
import com.example.coursework.database.entities.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}