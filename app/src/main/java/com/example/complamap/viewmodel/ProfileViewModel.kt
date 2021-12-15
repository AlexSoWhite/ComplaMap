package com.example.complamap.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.User
import com.example.complamap.model.UserManager
import com.example.complamap.model.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    fun getUser(callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = UserManager.getCurrentUser()
            callback(user)
        }
    }

    fun putUserToCache(user: User) {
        viewModelScope.launch {
            UserRepository.putUserToCache(user)
        }
    }

    fun deleteUserFromCache() {
        viewModelScope.launch {
            UserManager.deleteUserFromCache()
        }
    }

    fun loadPhotoFromServer(context: Context, user: User, container: ImageView) {
        viewModelScope.launch {
            Glide.with(context)
                .load(user.profilePic)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }

    fun updateUser(
        uri: Uri?,
        username: String,
        callback: (String) -> Unit
    ) {
        if (username.isEmpty() or username.isBlank()) {
            callback("имя пользователя не должно быть пустым")
        } else {
            viewModelScope.launch {
                UserRepository.updateUser(
                    uri,
                    username,
                    callback
                )
            }
        }
    }

    fun setUser(user: User?) {
        viewModelScope.launch {
            UserManager.setUser(user)
        }
    }

    fun addSubsToUser(userId: String, sub: String) {
        viewModelScope.launch {
            UserRepository.addSubsToUser(userId, sub)
        }
    }

    fun removeSubsFromUser(userId: String, sub: String) {
        viewModelScope.launch {
            UserRepository.removeSubsFromUser(userId, sub)
        }
    }

    fun getUserFromDatabase(userId: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = UserRepository.getUserFromDatabase(userId)
            callback(user)
            return@launch
        }
    }

    fun editRating(userId: String, rating: Int) {
        viewModelScope.launch {
            UserRepository.editRating(userId, rating)
        }
    }
}
