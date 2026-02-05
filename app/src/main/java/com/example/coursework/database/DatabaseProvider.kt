package com.example.coursework.database

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room

object DatabaseProvider {

    private var database: AppDatabase? = null

    fun getDatabase(applicationContext: Context): AppDatabase {
        if(database == null) {
            database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).allowMainThreadQueries().build()
        }

        return database!!
    }
}