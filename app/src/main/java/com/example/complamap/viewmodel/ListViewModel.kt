package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Complaint
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private var filter: Filter? = null

    fun getComplaints(callback: (List<Complaint>) -> Unit) {
        viewModelScope.launch {
            val query: Task<QuerySnapshot>?
            val collection = FirebaseFirestore
                .getInstance()
                .collection("complaint")
            query = if (filter != null) {
                collection
                    .whereEqualTo(filter!!.key, filter!!.value)
                    .get()
            } else {
                collection
                    .orderBy("creation_date")
                    .limit(10)
                    .get()
            }
            query.addOnCompleteListener { task ->
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

    fun setFilter(filter: Filter?) {
        this.filter = filter
    }

    data class Filter(val key: String, val value: Any)
}
