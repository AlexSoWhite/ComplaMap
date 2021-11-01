package com.example.complamap

import android.net.Uri

data class User(
    val username: String? = null,
    val email: String? = null,
    val rating: Int? = null,
    val profilePic: Uri? = null,
    val subs: Array<String>? = null
)