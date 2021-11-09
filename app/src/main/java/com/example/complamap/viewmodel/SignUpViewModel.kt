package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.AppRepository
import com.example.complamap.model.LoginResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    fun register(
        email: String,
        username: String,
        password: String,
        repeatedPassword: String,
        callback: (result: LoginResult) -> Unit
    ) {
        if (email.isEmpty() || email.isBlank()) {
            val res = LoginResult.Error("введите почту")
            callback(res)
            return
        }
        if (username.isEmpty() || username.isBlank()) {
            val res = LoginResult.Error("введите имя пользователя")
            callback(res)
            return
        }
        if (password.isEmpty() || password.isBlank()) {
            val res = LoginResult.Error("введите пароль")
            callback(res)
            return
        }
        if (repeatedPassword.isEmpty() || repeatedPassword.isBlank()) {
            val res = LoginResult.Error("повторите пароль")
            callback(res)
            return
        }
        if (repeatedPassword != password) {
            val res = LoginResult.Error("пароли не совпадают")
            callback(res)
            return
        }
        viewModelScope.launch {
            AppRepository.register(email, password, username, callback)
            return@launch
        }
    }
}
