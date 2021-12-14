package com.example.complamap.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.ComplaintRepository
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class ComplaintViewModel : ViewModel() {

    fun putComplaintToDatabase(
        complaint: Complaint,
        uri: Uri?,
        callback: (Int) -> Unit
    ) {
        viewModelScope.launch {
            complaint.creation_date = Timestamp.now()
            complaint.creation_day = android.text.format.DateFormat.format(
                "dd.MM.yyyy",
                complaint.creation_date!!.toDate()
            ).toString()
            ComplaintRepository.addComplaintToDatabase(complaint) { compId ->
                complaint.compId = compId
                ComplaintManager.setComplaint(complaint)
                if (uri != null) {
                    sendPhoto(uri, compId) {
                        complaint.photo = it
                        ComplaintRepository.addPhoto(compId, it, callback)
                    }
                } else {
                    callback(AppCompatActivity.RESULT_OK)
                }
            }
        }
    }

    private fun sendPhoto(
        uri: Uri,
        complaintId: String,
        callback: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val pictureRef = storageRef.child("images/$complaintId")

        val uploadTask = pictureRef.putFile(uri)

        var photoString = ""

        uploadTask.addOnSuccessListener {
            pictureRef.downloadUrl.addOnSuccessListener {
                photoString = it.toString()
                callback(photoString)
            }
        }
    }

    fun loadPhotoFromUri(context: Context, uri: Uri?, container: ImageView) {
        viewModelScope.launch {
            Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }

    fun loadPhotoFromServer(context: Context, complaint: Complaint, container: ImageView) {
        viewModelScope.launch {
            Glide.with(context)
                .load(complaint.photo)
                .placeholder(R.drawable.default_placeholder)
                .into(container)
        }
    }

    fun deleteComplaint(complaintId: String) {
        viewModelScope.launch {
            ComplaintRepository.deleteComplaintFromDatabase(complaintId)
        }
    }

    fun editComplaint(
        complaint: Complaint,
        uri: Uri?,
        callback: (String) -> Unit
    ) {
        viewModelScope.launch {

            if (uri != null) {
                complaint.compId?.let { id ->
                    sendPhoto(uri, id) {
                        ComplaintRepository.editComplaint(
                            it,
                            complaint,
                            Timestamp.now(),
                            android.text.format.DateFormat.format(
                                "dd.MM.yyyy",
                                Timestamp.now().toDate()
                            ).toString(),
                            callback
                        )
                    }
                }
            } else {
                ComplaintRepository.editComplaint(
                    "",
                    complaint,
                    Timestamp.now(),
                    android.text.format.DateFormat.format(
                        "dd.MM.yyyy",
                        Timestamp.now().toDate()
                    ).toString(),
                    callback
                )
            }
        }
    }

    fun addFollowers(complaintId: String, follower: String) {
        viewModelScope.launch {
            ComplaintRepository.addFollowers(complaintId, follower)
        }
    }

    fun removeFollowers(complaintId: String, follower: String) {
        viewModelScope.launch {
            ComplaintRepository.removeFollowers(complaintId, follower)
        }
    }

    fun editVotes(
        complaintId: String,
        votePair: Pair<String, Long>,
        member: String,
        userId: String,
        flag: Boolean
    ) {
        viewModelScope.launch {
            ComplaintRepository.editVotes(complaintId, votePair, member, userId, flag)
        }
    }
}
