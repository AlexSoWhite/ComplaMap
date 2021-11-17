package com.example.complamap.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

object ComplaintRepository: ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun getComplaintFromDatabase(complaintId: String){
        db.collection("complaint").document(complaintId).get()
    }
    fun addComplaintToDatabase(complaint: Complaint){
        db.collection("complaint").add(complaint) //TODO
    }
    }