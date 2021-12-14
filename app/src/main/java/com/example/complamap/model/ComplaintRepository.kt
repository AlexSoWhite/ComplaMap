package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object ComplaintRepository : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun addComplaintToDatabase(complaint: Complaint, callback: (String) -> Unit) {
        db.collection("complaint").add(complaint).addOnSuccessListener { docRef ->
            db.collection("complaint").document(docRef.id).update("compId", docRef.id)
            callback(docRef.id)
        }
    }

    fun addPhoto(compId: String, url: String, callback: (Int) -> Unit) {
        ComplaintManager.getCurrentComplaint()?.photo = url
        db.collection("complaint").document(compId).update("photo", url).addOnSuccessListener {
            callback(AppCompatActivity.RESULT_OK)
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
        editTimestamp: com.google.firebase.Timestamp,
        editDay: String
    ) {
        if (uri != "") {
            ComplaintManager.getCurrentComplaint()?.photo = uri
            db.collection("complaint").document(complaintId).update(
                mapOf(
                    "photo" to uri,
                    "description" to description,
                    "address" to address,
                    "category" to category,
                    "editTimestamp" to editTimestamp,
                    "editDay" to editDay
                )
            ).addOnCompleteListener {
                viewModelScope.launch {
                    ComplaintManager.setComplaint(
                        getComplaintFromDatabase(complaintId)
                    )
                }
            }
        } else {
            db.collection("complaint").document(complaintId).update(
                mapOf(
                    "description" to description,
                    "address" to address,
                    "category" to category,
                    "editTimestamp" to editTimestamp,
                    "editDay" to editDay
                )
            ).addOnCompleteListener {
                viewModelScope.launch {
                    ComplaintManager.setComplaint(
                        getComplaintFromDatabase(complaintId)
                    )
                }
            }
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

    suspend fun getComplaintFromDatabase(complaintId: String): Complaint? {
        val complData = db.collection("complaint").document(complaintId).get().await()
        return complData.toObject(Complaint::class.java)
    }
}
