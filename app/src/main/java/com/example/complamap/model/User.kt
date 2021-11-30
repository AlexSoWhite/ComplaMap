package com.example.complamap.model

data class User(
    var username: String? = null,
    val email: String? = null,
    var profilePic: String? = null,
    val rating: Double? = null,
    val subs: List<String>? = null,
    val uid: String? = null
)
