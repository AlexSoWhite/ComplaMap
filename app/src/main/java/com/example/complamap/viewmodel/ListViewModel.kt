package com.example.complamap.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Complaint
import com.example.complamap.model.ListRepository
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private var filter: Filter? = null

    fun getComplaints(callback: (List<Complaint>) -> Unit) {
        when {
            filter == null -> ListRepository.getComplaintsWithNoFilter(callback)

            filter!!.value == "default" -> ListRepository.getComplaintsWithDefaultFilter(callback)

            else -> ListRepository.getComplaintsWithEqualFilter(this.filter!!, callback)
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
