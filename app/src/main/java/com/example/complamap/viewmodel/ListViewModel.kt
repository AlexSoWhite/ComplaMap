package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.Complaint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    fun getComplaints(callback: (List<Complaint>) -> Unit) {
        viewModelScope.launch {
            FirebaseFirestore
                .getInstance()
                .collection("complaint")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list: List<Complaint> = task
                            .result
                            ?.toObjects(Complaint::class.java)
                            as List<Complaint>
                        callback(list)
                    }
                }
        }
    }
}
