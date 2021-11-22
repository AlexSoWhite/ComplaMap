package com.example.complamap.model

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

object MapRepository {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun getComplaintWithLocation(callback: (List<Complaint>) -> Unit){
        db
            .collection("complaint")
            .whereNotEqualTo("location", null)
            .get()
            .addOnCompleteListener {
                completeListener(it, callback)
        }
    }

    private fun completeListener(
        task: Task<QuerySnapshot>,
        callback: (List<Complaint>) -> Unit
    ) {
        if (task.isSuccessful) {
            val list: List<Complaint> = task
                .result
                ?.toObjects(Complaint::class.java)
                    as List<Complaint>
            callback(list)
        }
    }
}