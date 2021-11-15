package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.User
import com.example.complamap.model.UserManager
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    fun getUser(callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = UserManager.getCurrentUser()
            callback(user)
            return@launch
        }
    }

    fun deleteUserFromCache() {
        viewModelScope.launch {
            UserManager.deleteUserFromCache()
        }
    }

    fun serUser(user: User?) {
        viewModelScope.launch {
            UserManager.setUser(user)
        }
    }
}
