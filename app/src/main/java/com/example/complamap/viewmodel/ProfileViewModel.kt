package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Complaint
import com.example.complamap.model.User
import com.example.complamap.model.UserManager
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    fun getUser(callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = UserManager.getCurrentUser()
            callback(user)
            return@launch
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

    fun setUser(user: User?) {
        viewModelScope.launch {
            UserManager.setUser(user)
        }
    }
}
