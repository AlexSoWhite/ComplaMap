package com.example.complamap

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class User (
        val username: String? = null,
        val email: String? = null,
        val profilePic: String? = null,
        val rating: Long? = null,
        val subs: List<DocumentReference>? = null,
        )