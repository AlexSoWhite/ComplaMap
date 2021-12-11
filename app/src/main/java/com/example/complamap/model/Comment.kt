package com.example.complamap.model

import com.google.firebase.Timestamp


data class Comment (
    val complaintId: String? = null,
    val authorId: String? = null,
    val comment_text: String? = null,
    val date: String? = null,
    val timestamp: Timestamp? = Timestamp.now() // ?
    )