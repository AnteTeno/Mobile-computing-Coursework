package com.example.coursework.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.coursework.database.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE username LIKE :username LIMIT 1")
    fun findByName(username: String): User

    @Query("SELECT * FROM user WHERE uid = 1")
    fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun delete(user: User)


}