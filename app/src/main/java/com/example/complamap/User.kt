package com.example.complamap

data class User(
    var username: String? = null,
    var email: String? = null,
    var rating: Int? = null,
    var profilePic: String? = null,
    var subs: Array<String>? = null
)
