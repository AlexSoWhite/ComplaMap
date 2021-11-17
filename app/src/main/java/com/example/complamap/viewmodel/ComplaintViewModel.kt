package com.example.complamap.viewmodel

import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complamap.model.*
import com.example.complamap.views.activities.CreateComplaintActivity
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.io.File

class ComplaintViewModel() : ViewModel() {

private lateinit var tempImageFilePath: String
fun setTempImageFilePath(string: String){
    tempImageFilePath = string
}
    fun getTempImageFilePath(): String{
        return tempImageFilePath
    }

    fun putComplaintToDatabase(complaint: Complaint ){
        viewModelScope.launch {
            complaint.creation_date = Timestamp.now()
            complaint.creation_day = android.text.format.DateFormat.format(
                "dd.MM.yyyy",
                Timestamp.now().toDate()
            ).toString()
            ComplaintRepository.addComplaintToDatabase(complaint)
            return@launch
        }
    }

    private fun createImageFile(): File {
        val storageDir = ContextContainer.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

    fun sendPhoto(): String{
        val storageRef = FirebaseStorage.getInstance().reference
        val file = Uri.fromFile(File(tempImageFilePath))
        val pictureRef = storageRef.child("images/${file.lastPathSegment}")

        val tempImageUri = FileProvider.getUriForFile(
            ContextContainer.getContext(),
            "com.example.complamap.provider",
            createImageFile().also {
                tempImageFilePath = it.absolutePath
            }
        )

        val uploadTask = pictureRef.putFile(tempImageUri!!)

        var PhotoString: String = ""

        uploadTask.addOnSuccessListener {
            pictureRef.downloadUrl.addOnSuccessListener {
                PhotoString = it.toString()
            }
        }
        return PhotoString
    }
}