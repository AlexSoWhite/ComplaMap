package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.complamap.model.AppRepository

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private var appRepository: AppRepository = AppRepository(application)

    fun login(email: String, password: String) {
        appRepository.login(email, password)
    }
}