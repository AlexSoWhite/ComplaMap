package com.example.complamap.model

object ComplaintManager {

    private var complaint: Complaint? = null

    var justPublished: Boolean = false
    fun getCurrentComplaint(): Complaint? {
        justPublished = false
        return complaint
    }

    fun setComplaint(complaint: Complaint?) {
        this.complaint = complaint
    }
}
