package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.complamap.model.User
import com.example.complamap.model.AppRepository
import com.example.complamap.model.UserManager

class ProfileViewModel() : ViewModel() {

    fun getUser(): User? {
        return UserManager.getCurrentUser()
    }
}
