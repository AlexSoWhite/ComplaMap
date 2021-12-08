package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Comment
import com.example.complamap.model.ComplaintRepository
import kotlinx.coroutines.launch

class CommentViewModel: ViewModel() {

    fun loadAuthorProfilePic(context: Context, comment: Comment, container: ImageView) {
        viewModelScope.launch {
            Glide.with(context)
                .load(comment.author!!.profilePic)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }
    fun addComment(
        complaintId: String,
        comment: Comment
    ) {
        viewModelScope.launch {
            ComplaintRepository.addComment(complaintId, comment)
        }
    }
}