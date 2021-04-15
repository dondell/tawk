package com.dmj.tawktodevtest.data.models

import androidx.lifecycle.LiveData

class ProfileRepository(private val profileDao: ProfileDao) {

    suspend fun addProfile(profile: Profile) {
        profileDao.addProfile(profile)
    }

    fun searchProfile(query: String): LiveData<List<Profile>> {
        return profileDao.searchProfile(query)
    }

}