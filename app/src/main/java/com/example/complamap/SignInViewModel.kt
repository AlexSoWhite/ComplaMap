package com.example.complamap

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private var appRepository: AppRepository = AppRepository(application)

    fun login(email: String, password: String) {
        appRepository.login(email, password)
    }
}