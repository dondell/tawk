package com.dmj.tawktodevtest.data.models

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    var readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun addUsers(users: List<User>) {
        userDao.addUsers(users)
    }

    fun searchUser(query: String): LiveData<List<User>> {
        return userDao.searchUser(query)
    }

}