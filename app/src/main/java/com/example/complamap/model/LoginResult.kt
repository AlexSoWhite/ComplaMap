package com.example.complamap.model

sealed class LoginResult {
    data class Error(val message: String): LoginResult();
    object Success : LoginResult()
}