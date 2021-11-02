package com.example.complamap.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.AppRepository
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : ViewModel() {
    private var appRepository: AppRepository = AppRepository(application)
    var state: MutableLiveData<Boolean> = MutableLiveData()

    fun login(email: String, password: String) {
         viewModelScope.launch {
            appRepository.login(email, password)
            state.postValue(true)
        }
    }
}
