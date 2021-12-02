package com.example.complamap.model

data class User(
    val username: String? = null,
    val email: String? = null,
    val profilePic: String? = null,
    val rating: Double? = null,
    val subs: MutableList<String>? = null,
    val uid: String? = null
)
