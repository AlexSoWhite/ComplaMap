package com.example.complamap.model

import com.google.firebase.firestore.DocumentReference

data class User(
    val username: String? = null,
    val email: String? = null,
    val profilePic: String? = null,
    val rating: Long? = null,
    val subs: List<DocumentReference>? = null,
)
