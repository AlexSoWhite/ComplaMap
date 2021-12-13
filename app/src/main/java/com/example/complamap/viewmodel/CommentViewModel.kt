package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Comment
import com.example.complamap.model.CommentRepository
import com.example.complamap.model.User
import com.example.complamap.model.UserRepository
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {

    fun loadAuthorProfilePic(context: Context, comment: Comment, container: ImageView) {
        viewModelScope.launch {
            val user = UserRepository.getUserFromDatabase(comment.authorId!!)
            Glide.with(context)
                .load(user?.profilePic)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }

    fun loadUserInfo(uid: String, callback: (User) -> Unit) {
        viewModelScope.launch {
            callback(UserRepository.getUserFromDatabase(uid)!!)
        }
    }

    fun addComment(
        complaintId: String,
        comment: Comment
    ) {
        viewModelScope.launch {
            CommentRepository.addComment(complaintId, comment)
        }
    }

    fun getComments(
        complaintId: String,
        callback: (List<Comment>) -> Unit
    ) {
        viewModelScope.launch {
            CommentRepository.getComments(complaintId, callback)
        }
    }
}
