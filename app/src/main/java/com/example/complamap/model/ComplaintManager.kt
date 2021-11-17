package com.example.complamap.model

object ComplaintManager {

    private var complaint: Complaint? = null

    fun getCurrentComplaint(): Complaint? {
        return complaint
    }

    fun setComplaint(complaint: Complaint?) {
        this.complaint = complaint
    }
}
