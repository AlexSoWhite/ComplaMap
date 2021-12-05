package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.User
import com.example.complamap.model.UserManager
import com.example.complamap.model.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    fun getUser(callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = UserManager.getCurrentUser()
            callback(user)
            return@launch
        }
    }

    fun putUserToCache(user: User) {
        viewModelScope.launch {
            UserRepository.putUserToCache(user)
        }
    }

    fun deleteUserFromCache() {
        viewModelScope.launch {
            UserManager.deleteUserFromCache()
        }
    }

    fun setUser(user: User?) {
        viewModelScope.launch {
            UserManager.setUser(user)
        }
    }

    fun addSubsToUser(userId: String, sub: String) {
        viewModelScope.launch {
            UserRepository.addSubsToUser(userId, sub)
        }
    }

    fun removeSubsFromUser(userId: String, sub: String) {
        viewModelScope.launch {
            UserRepository.removeSubsFromUser(userId, sub)
        }
    }

    fun getUserFromDatabase(userId: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = UserRepository.getUserFromDatabase(userId)
            callback(user)
            return@launch
        }
    }

    fun editRating(userId: String, rating: Double) {
        viewModelScope.launch {
            UserRepository.editRating(userId, rating)
        }
    }
}
