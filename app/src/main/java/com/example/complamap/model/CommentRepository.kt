package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

object CommentRepository : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

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

    private fun getCommentCollection(): CollectionReference {
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
