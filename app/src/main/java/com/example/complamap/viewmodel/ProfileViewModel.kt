package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.complamap.User
import com.example.complamap.model.AppRepository

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private var appRepository: AppRepository = AppRepository(application)

    suspend fun getUser(): User? {
        return appRepository.getUser()
    }
}