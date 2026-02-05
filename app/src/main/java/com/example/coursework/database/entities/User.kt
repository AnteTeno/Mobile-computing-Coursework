package com.example.coursework.database.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "profile_picture_path") val profilePicturePath: String
)