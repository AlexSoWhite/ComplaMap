package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import kotlinx.coroutines.launch

class ComplaintViewModel() : ViewModel() {

    fun getComplaint(callback: (Complaint?) -> Unit ) {
        viewModelScope.launch {
            val complaint = ComplaintManager.getCurrentComplaint()
            callback(complaint)
            return@launch
        }

    }
}