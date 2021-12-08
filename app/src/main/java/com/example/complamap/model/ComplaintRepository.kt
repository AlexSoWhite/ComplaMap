package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object ComplaintRepository : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun getComplaintFromDatabase(complaintId: String) {
        db.collection("complaint").document(complaintId).get()
    }

    fun addComplaintToDatabase(complaint: Complaint, callback: (String) -> Unit) {
        db.collection("complaint").add(complaint).addOnSuccessListener { docRef ->
            db.collection("complaint").document(docRef.id).update("compId", docRef.id)
            callback(docRef.id)
        }
    }

    fun addPhoto(compId: String, url: String, callback: (String) -> Unit) {
        db.collection("complaint").document(compId).update("photo", url).addOnSuccessListener {
            callback("Опубликовано")
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

    fun addFollowers(complaintId: String, follower: String) {
        db.collection("complaint").document(complaintId).update(
            "followers",
            FieldValue.arrayUnion(follower)
        )
    }

    fun removeFollowers(complaintId: String, follower: String) {
        db.collection("complaint").document(complaintId).update(
            "followers",
            FieldValue.arrayRemove(follower)
        )
    }

    fun editVotes(
        complaintId: String,
        field: String,
        number: Long,
        member: String,
        userId: String,
        flag: Boolean
    ) {
        db.collection("complaint").document(complaintId).update(
            mapOf(field to number)
        )
        if (flag) {
            db.collection("complaint").document(complaintId).update(
                member,
                FieldValue.arrayUnion(userId)
            )
        } else {
            db.collection("complaint").document(complaintId).update(
                member,
                FieldValue.arrayRemove(userId)
            )
        }
    }
    fun addComment(
        complaintId: String,
        comment: Comment
    ) {
        db.collection("comment").add(comment).addOnSuccessListener { docRef ->
            db.collection("comment").document(docRef.id).update("author", complaintId)
        }
    }
}
