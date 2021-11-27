package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

object ComplaintRepository : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun getComplaintFromDatabase(complaintId: String) {
        db.collection("complaint").document(complaintId).get()
    }

    fun addComplaintToDatabase(complaint: Complaint) {
        db.collection("complaint").add(complaint).addOnSuccessListener { docRef ->
            db.collection("complaint").document(docRef.id).update("compId", docRef.id)
        }
    }

    fun deleteComplaintFromDatabase(complaintId: String) {
        db.collection("complaint").document(complaintId).delete()
    }

    fun editComplaint(
        complaintId: String,
        uri: String,
        description: String,
        address: String,
        category: String,
        edit_date: com.google.firebase.Timestamp,
        edit_day: String
    ) {
        if (uri != "") {
            db.collection("complaint").document(complaintId).update(
                mapOf(
                    "photo" to uri,
                    "description" to description,
                    "address" to address,
                    "category" to category,
                    "edit_date" to edit_date,
                    "edit_day" to edit_day
                )
            )
        } else {
            db.collection("complaint").document(complaintId).update(
                mapOf(
                    "description" to description,
                    "address" to address,
                    "category" to category,
                    "edit_date" to edit_date,
                    "edit_day" to edit_day
                )
            )
        }
    }
}
