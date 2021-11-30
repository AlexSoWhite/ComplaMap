package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.LoginResult
import com.example.complamap.model.UserRepository
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

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
        } else if (username.isEmpty() || username.isBlank()) {
            val res = LoginResult.Error("введите имя пользователя")
            callback(res)
        } else if (password.isEmpty() || password.isBlank()) {
            val res = LoginResult.Error("введите пароль")
            callback(res)
        } else if (repeatedPassword.isEmpty() || repeatedPassword.isBlank()) {
            val res = LoginResult.Error("повторите пароль")
            callback(res)
        } else if (repeatedPassword != password) {
            val res = LoginResult.Error("пароли не совпадают")
            callback(res)
        } else {
            viewModelScope.launch {
                UserRepository.register(email, password, username, callback)
                return@launch
            }
        }
    }
}
