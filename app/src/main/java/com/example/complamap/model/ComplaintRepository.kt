package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

object ComplaintRepository: ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun getComplaintFromDatabase(complaintId: String){
        db.collection("complaint").document(complaintId).get()
    }

    fun addComplaintToDatabase(complaint: Complaint){
        db.collection("complaint").add(complaint) //TODO
    }

}