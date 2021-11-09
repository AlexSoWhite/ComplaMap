package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.AppRepository
import com.example.complamap.model.LoginResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    fun register(email: String, password: String, username: String, callback: (result: LoginResult) -> Unit) {
        if (email.isEmpty() && email.isBlank()) {
            val res = LoginResult.Error("введите логин")
            callback(res)
            return
        }
        if (password.isEmpty() && password.isBlank()) {
            val res = LoginResult.Error("введите пароль")
            callback(res)
            return
        }
        if (username.isEmpty() && username.isBlank()) {
            val res = LoginResult.Error("введите имя пользователя")
            callback(res)
            return
        }
        viewModelScope.launch {
            AppRepository.register(email, password, username, callback)
            return@launch
        }
    }
}
