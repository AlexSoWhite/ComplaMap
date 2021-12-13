package com.example.complamap.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Complaint(
    val compId: String? = null,
    val category: String? = null,
    var photo: String? = null,
    var creationTimestamp: Timestamp? = null,
    val location: GeoPoint? = null,
    val description: String? = null,
    var approvals: Long? = null,
    val approvers: MutableList<String>? = null,
    var rejections: Long? = null,
    val rejecters: MutableList<String>? = null,
    val status: String? = null,
    val followers: MutableList<String>? = null,
    var creator: String? = null,
    var address: String? = null,
    var creationDay: String? = null,
    var editTimestamp: Timestamp? = null,
    var editDay: String? = null,
)
