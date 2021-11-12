package com.example.complamap.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.orhanobut.hawk.Hawk

object ComplaintRepository: ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun acceptComplaint(complaint: Complaint){
        addComplaintToDatabase(complaint)
        putComplaintToCache(complaint)
        ComplaintManager.setComplaint(complaint)

    }

    private fun addComplaintToDatabase(complaint: Complaint){
        db.collection("complaint").add(complaint)
    }

    private fun putComplaintToCache(complaint: Complaint){
        Hawk.put("complaint", complaint)
    }


    private suspend fun convertReferenceToComplaint():Complaint{
        val complaintData = ComplaintManager.getCurrentComplaint()
        val category = complaintData!!.category
        val creation_date: Timestamp? = null
        val location: GeoPoint? = null
        val description: String? = null
        val approvals: Long? = null
        val rejections: Long? = null
        val status: String? = null
        val followers: List<DocumentReference>? = null
        val creator: DocumentReference? = null
        return Complaint(
            category,
            creation_date,
            location,
            description,
            approvals,
            rejections,
            status,
            followers,
            creator
        )
    }



}