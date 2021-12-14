package com.example.complamap.views.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.complamap.R
import com.example.complamap.databinding.ActivityPopUpTakePhotoBinding
import java.io.File

class TakePhotoActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var filePath: String? = null
    private lateinit var binding: ActivityPopUpTakePhotoBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri?>
    private lateinit var galleryLauncher: ActivityResultLauncher<String?>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_pop_up_take_photo
        )

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                handlePhotoResult(success)
            }

        galleryLauncher =
            registerForActivityResult(PickPhotoActivityContract()) {
                imageUri = it
                handlePhotoResult(true)
            }

        binding.cameraBtn.setOnClickListener {
            launchCamera()
        }

        binding.galleryBtn.setOnClickListener {
            launchGallery()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun launchCamera() {
        imageUri = FileProvider.getUriForFile(
            this,
            "com.example.complamap.provider",
            createImageFile().also {
                filePath = it.absolutePath
            }
        )
        cameraPermission.launch(Manifest.permission.CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    // user granted permission
                    cameraLauncher.launch(imageUri)
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    // do something if user denied permission and set Don't ask again
                    Toast.makeText(
                        this,
                        "вы запретили доступ к камере",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(
                        this,
                        "чтобы предоставить приложению доступ к камере, сбросьте данные приложения в настройках",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    // do something if permission for camera denied
                    requestPermissions(Array(1) { CAMERA_SERVICE }, 0)
                }
            }
        }

    private fun handlePhotoResult(isSuccess: Boolean) {
        when (isSuccess) {
            true -> {
                val data = Intent()
                data.putExtra("uri", imageUri.toString())
                setResult(RESULT_OK, data)
                this.finish()
            }
            false -> {
                val data = Intent()
                setResult(RESULT_CANCELED, data)
                this.finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun launchGallery() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(
                    this,
                    "вы запретили доступ к хранилищу",
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    this,
                    "чтобы предоставить приложению доступ к хранилищу, сбросьте данные приложения в настройках",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // No explanation needed; request the permission
                requestPermissions(
                    Array(size = 1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    0
                )
            }
        } else {
            // Permission has already been granted
            galleryLauncher.launch("")
        }
    }

    class PickPhotoActivityContract : ActivityResultContract<String, Uri?>() {

        override fun createIntent(context: Context, input: String) =
            Intent(
                Intent.ACTION_PICK
            ).apply { type = "image/*" }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) {
                return intent?.data
            }
            return null
        }
    }

    private fun createImageFile(): File {
        val storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }
}
