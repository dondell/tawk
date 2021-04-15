package com.dmj.tawktodevtest.data.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsers(users: List<User>)

    @Query("SELECT user_table.*, profile_table.notes AS notes FROM user_table LEFT JOIN profile_table ON profile_table.login = user_table.login ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    @Query("SELECT user_table.*, profile_table.notes AS notes FROM user_table LEFT JOIN profile_table ON profile_table.login = user_table.login WHERE user_table.login LIKE :query OR profile_table.notes LIKE :query ORDER BY id ASC")
    fun searchUser(query: String): LiveData<List<User>>
}