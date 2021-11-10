package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.LoginResult
import com.example.complamap.model.UserRepository
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    fun login(
        email: String,
        password: String,
        callback: (result: LoginResult) -> Unit
    ) {
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
        viewModelScope.launch {
            UserRepository.login(email, password, callback)
            return@launch
        }
    }
}
