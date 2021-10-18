package com.example.complamap

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentPhotoBinding
import java.io.File

class PhotoFragment: Fragment(R.layout.fragment_photo) {

    //permission for camera
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                // user granted permission
                cameraLauncher.launch(tempImageUri)
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                //do something if user denied permission and set Don't ask again
            }
            else -> {
                //do something if permission for camera denied
            }
        }
    }

    //TODO these launchers here are just for example, they only put images instead of a cat
    //uri for setting image content by setImageUri
    private var tempImageUri : Uri? = null
    //path to photo for sending to db
    private var tempImageFilePath = ""
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if(success) {
            binding.img.setImageURI(tempImageUri)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if(it != null) {
            tempImageUri = it
            binding.img.setImageURI(it)
        }
    }

    private lateinit var binding: FragmentPhotoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhotoBinding.inflate(inflater)
        binding.takePhotoCameraBtn.setOnClickListener {
            tempImageUri = FileProvider.getUriForFile(requireContext(), "com.example.complamap.provider", createImageFile().also {
                tempImageFilePath = it.absolutePath
            })
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                //do something in this case
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.takePhotoGalleryBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        if (savedInstanceState != null) {
            binding.img.setImageURI(savedInstanceState.getParcelable("temp_uri"))
        }

        return  binding.root
    }

    private fun createImageFile() : File {
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

    //TODO for some reason neither it doesn't work every time I want nor the loading from saved state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("temp_uri", tempImageUri)
    }
}