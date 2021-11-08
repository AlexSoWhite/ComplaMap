package com.example.complamap

import com.google.firebase.firestore.DocumentReference

data class User (
        var username: String? = null,
        var email: String? = null,
        var profilePic: String? = null,
        var rating: Long? = null,
        var subs: List<DocumentReference>? = null,
        )
