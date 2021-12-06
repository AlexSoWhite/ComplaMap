package com.example.complamap.model

import android.media.Image

data class Comment (
    val complaint: Complaint? = null,
    val author: User? = null,
    val comment_text: String? = null,
    val date: String? = null,
    val author_name: String = author?.username!!,
    val author_rating: String = author?.rating.toString(),
    val author_profile_pic: String? = author?.profilePic

)