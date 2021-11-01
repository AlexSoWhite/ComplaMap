package com.example.complamap

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private var appRepository: AppRepository = AppRepository(application)

    suspend fun getUser() {
        appRepository.getUser()
    }
}