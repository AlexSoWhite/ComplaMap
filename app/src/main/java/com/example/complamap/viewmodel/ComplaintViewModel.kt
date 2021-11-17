package com.example.complamap.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.model.*
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File

class ComplaintViewModel : ViewModel() {

    fun putComplaintToDatabase(
        complaint: Complaint,
        uri: Uri?,
        path: String?
    ) {
        viewModelScope.launch {
            complaint.creation_date = Timestamp.now()
            complaint.creation_day = android.text.format.DateFormat.format(
                "dd.MM.yyyy",
                complaint.creation_date!!.toDate()
            ).toString()
            if (uri != null) {
                sendPhoto(uri, path!!) {
                    complaint.photo = it
                    ComplaintRepository.addComplaintToDatabase(complaint)
                }
            } else {
                ComplaintRepository.addComplaintToDatabase(complaint)
            }
            return@launch
        }
    }

    private fun sendPhoto(
        uri: Uri,
        path: String,
        callback: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val file = Uri.fromFile(File(path))
        val pictureRef = storageRef.child("images/${file.lastPathSegment}")

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
}
