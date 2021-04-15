package com.dmj.tawktodevtest.data.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

    fun addUsers(users: List<User>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUsers(users)
        }
    }

    fun getAllUsers(): LiveData<List<User>> {
        return repository.readAllData;
    }

     fun searchUser(query: String): LiveData<List<User>> {
        return repository.searchUser("%$query%")
    }

}