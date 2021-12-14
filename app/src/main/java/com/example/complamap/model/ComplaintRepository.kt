package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot

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
                    "edit_date" to edit_date,
                    "edit_day" to edit_day
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
    fun addComment(
        complaintId: String,
        comment: Comment
    ) {
        db.collection("comment").add(comment)
            .addOnSuccessListener { docRef ->
                db.collection("comment").document(docRef.id).update("complaintId", complaintId)
            }
    }

    fun getComments(
        complaintId: String,
        callback: (List<Comment>) -> Unit
    ) {
        viewModelScope.launch {
            getCommentCollection()
                .whereEqualTo("complaintId", complaintId)
                .get()
                .addOnCompleteListener {
                    completeListener(it, callback)
                }
        }
    }

    fun getCommentCollection(): CollectionReference {
        return db.collection("comment")
    }

    private fun completeListener(
        task: Task<QuerySnapshot>,
        callback: (List<Comment>) -> Unit
    ) {
        if (task.isSuccessful) {
            val list: List<Comment> = task
                .result
                ?.toObjects(Comment::class.java) as List<Comment>
            callback(list.sortedBy { it.timestamp }) // колбэкаем сортированный здесь
        }
    }
}
