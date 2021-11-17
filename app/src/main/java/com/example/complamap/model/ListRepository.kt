package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.viewmodel.ListViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

object ListRepository : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun getComplaintsWithEqualFilter(
        filter: ListViewModel.Filter,
        callback: (List<Complaint>) -> Unit
    ) {
        viewModelScope.launch {
            getComplaintCollection()
                .whereEqualTo(filter.key, filter.value)
                .get()
                .addOnCompleteListener {
                    completeListener(it, callback)
                }
        }
    }

    fun getComplaintsWithDefaultFilter(
        callback: (List<Complaint>) -> Unit
    ) {
        viewModelScope.launch {
            getComplaintCollection()
                .orderBy("creation_date", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener {
                    completeListener(it, callback)
                }
        }
    }

    fun getComplaintsWithNoFilter(
        callback: (List<Complaint>) -> Unit
    ) {
        viewModelScope.launch {
            getComplaintCollection()
                .get()
                .addOnCompleteListener {
                    completeListener(it, callback)
                }
        }
    }

    private fun getComplaintCollection(): CollectionReference {
        return db.collection("complaint")
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