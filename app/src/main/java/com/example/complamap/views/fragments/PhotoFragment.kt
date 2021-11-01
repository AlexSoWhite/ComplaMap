package com.example.complamap.views.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.complamap.R
import com.example.complamap.databinding.FragmentPhotoBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    // permission for camera
    private val cameraPermission = registerForActivityResult(RequestPermission()) { granted ->
        when {
            granted -> {
                // user granted permission
                cameraLauncher.launch(tempImageUri)
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // do something if user denied permission and set Don't ask again
            }
            else -> {
                // do something if permission for camera denied
            }
        }
    }

    // TODO these launchers here are just for example, they only put images instead of a cat
    // uri for setting image content by setImageUri
    private var tempImageUri: Uri? = null
    // path to photo for sending to db
    private var tempImageFilePath = ""
    private val cameraLauncher = registerForActivityResult(TakePicture()) { success ->
        if (success) {
            binding.img.setImageURI(tempImageUri)
            binding.sendButton.visibility = VISIBLE
        }
    }

    private val galleryLauncher = registerForActivityResult(GetContent()) {
        if (it != null) {
            tempImageUri = it
            binding.img.setImageURI(it)
            val file = createImageFile()
            tempImageFilePath = file.absolutePath
            binding.sendButton.visibility = VISIBLE
        }
    }

    private lateinit var binding: FragmentPhotoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoBinding.inflate(inflater)

        // download the placeholder
        val url = "gs://complamap.appspot.com/default-placeholder.png"
        val storage = FirebaseStorage.getInstance()
        val picRef = storage.getReferenceFromUrl(url)
        /// local file to which our data from server will be placed
        val localFile = File.createTempFile("downloaded_image", null, null)
        picRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            binding.img.setImageURI(localFile.absolutePath.toUri())
        }.addOnFailureListener {
            // Handle any errors
            binding.img.setImageResource(R.drawable.photo)
        }

        binding.takePhotoCameraBtn.setOnClickListener {
            tempImageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.complamap.provider",
                createImageFile().also {
                    tempImageFilePath = it.absolutePath
                }
            )
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // do something in this case
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.takePhotoGalleryBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.sendButton.setOnClickListener {
            binding.sendStatus.text = "отправка..."
            val storageRef = storage.reference
            // temporary filename provided by createImageFile will be used as a filename on server, actual filename may differ
            var file = Uri.fromFile(File(tempImageFilePath))
            val pictureRef = storageRef.child("images/${file.lastPathSegment}")
            // tempImageUri sets in both gallery and camera launchers so if we access to the file by its Uri we get correct data
            val uploadTask = pictureRef.putFile(tempImageUri!!)
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener {
                binding.sendStatus.text = "не удалось отправить"
            }.addOnSuccessListener {
                binding.sendStatus.text = "успешно"
            }
        }

        return binding.root
    }

    private fun createImageFile(): File {
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }
}
