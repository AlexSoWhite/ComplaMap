package com.example.complamap.viewmodel

import androidx.lifecycle.ViewModel
import com.example.complamap.model.Complaint

class ListViewModel : ViewModel() {

    fun getComplaints(): List<Complaint> {
        return listOf(
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint()
        )
    }

}