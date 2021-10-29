package com.example.complamap

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Complaint(
    val title: String? = null,
    val category: String? = null,
    val creation_date: Timestamp? = null,
    val location: GeoPoint? = null,
    val description: String? = null,
    val approvals: Long? = null,
    val rejections: Long? = null,
    val status: String? = null,
    val followers: List<DocumentReference>? = null,
    val user_ID: String? = null,
)