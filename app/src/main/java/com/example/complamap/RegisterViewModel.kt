package com.example.complamap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private var appRepository: AppRepository = AppRepository(application)
    private var userMutableLiveData: MutableLiveData<FirebaseUser> = appRepository.getUserMutableLiveData()

    fun register(email: String, password: String, username: String) {
        appRepository.register(email, password, username)
    }
}