package com.example.complamap.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Complaint(
    val compId: String? = null,
    val category: String? = null,
    var photo: String? = null,
    var creation_date: Timestamp? = null,
    val location: GeoPoint? = null,
    val description: String? = null,
    var approvals: Long? = null,
    val approvers: MutableList<String>? = null,
    var rejections: Long? = null,
    val rejecters: MutableList<String>? = null,
    val status: String? = null,
    val followers: MutableList<String>? = null,
    val creator: String? = null,
    var address: String? = null,
    var creation_day: String? = null,
    var edit_date: Timestamp? = null,
    var edit_day: String? = null,
    //var comments: MutableList<Comment> = mutableListOf()
)
