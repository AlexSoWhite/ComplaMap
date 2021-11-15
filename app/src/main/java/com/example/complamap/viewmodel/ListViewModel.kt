package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.databinding.ListItemBinding
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

    fun loadPhoto(context: Context, complaint: Complaint, container: ImageView) {
        viewModelScope.launch {
            Glide.with(context)
                .load(complaint.photo)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }
}
