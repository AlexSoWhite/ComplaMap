package com.example.complamap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.AppRepository
import com.example.complamap.model.LoginResult
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    var state: MutableLiveData<Boolean> = MutableLiveData()

    fun login(email: String, password: String, callback: (result: LoginResult) -> Unit) {
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
            AppRepository.login(email, password, callback)
            return@launch
        }
    }
}
