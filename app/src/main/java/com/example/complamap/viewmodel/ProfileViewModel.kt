package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import com.example.complamap.model.User
import com.example.complamap.model.UserManager

class ProfileViewModel() : ViewModel() {

    fun getUser(): User? {
        return UserManager.getCurrentUser()
    }
}
