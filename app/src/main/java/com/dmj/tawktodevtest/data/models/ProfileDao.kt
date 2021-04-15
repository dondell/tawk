package com.dmj.tawktodevtest.data.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProfile(profile: Profile)

    @Query("SELECT * FROM profile_table WHERE login = :query limit 1")
    fun searchProfile(query: String): LiveData<List<Profile>>

}