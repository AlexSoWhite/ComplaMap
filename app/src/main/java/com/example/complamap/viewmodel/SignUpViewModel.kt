package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.complamap.model.AppRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private var appRepository: AppRepository = AppRepository(application)

    fun register(email: String, password: String, username: String) {
        GlobalScope.launch {
            appRepository.register(email, password, username)
        }
    }
}
