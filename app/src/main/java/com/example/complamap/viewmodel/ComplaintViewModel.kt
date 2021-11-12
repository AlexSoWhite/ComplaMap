package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager

class ComplaintViewModel() : ViewModel() {

    fun GetComplaint(): Complaint? {
        return ComplaintManager.getCurrentComplaint()
    }
}