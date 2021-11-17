package com.example.complamap.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.transition.TransitionManager
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.CreateComplaintActivityBinding
import com.example.complamap.model.Complaint
import com.example.complamap.views.fragments.AddPlacemarkDialog
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.complamap.model.*
import com.example.complamap.model.ComplaintManager.setComplaint
import com.example.complamap.model.UserManager.getCurrentUser
import com.example.complamap.viewmodel.ComplaintViewModel
import java.io.File

class CreateComplaintActivity : AppCompatActivity() {

    private var isDialogShowing: Boolean = false
    // uri for setting image content by setImageUri
    private var tempImageUri: Uri? = null
    // path to photo for sending to db
    private var tempImageFilePath = ""

    // permission for camera
    @RequiresApi(Build.VERSION_CODES.M)
    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
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
                    requestPermissions(Array(1) { CAMERA_SERVICE }, 0)
                }
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.Image.setImageURI(tempImageUri)
            }
        }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            tempImageUri = it
            binding.Image.setImageURI(it)
            val file = createImageFile()
            tempImageFilePath = file.absolutePath
        }
    }

    private lateinit var binding: CreateComplaintActivityBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ExitButton.setOnClickListener {
            finish()
        }

        binding.AddPhotoButton.setOnClickListener {
            if (!isDialogShowing) {
                showDialog()
            }
        }

        binding.RootFrame.foreground.alpha = 0

        binding.AddButton.setOnClickListener { // обработка нажатия на кнопку внизу
            if (binding.Address.length() == 0) {
                Toast.makeText(applicationContext, "Введите адрес", Toast.LENGTH_SHORT).show()
            } else {
                binding.RootFrame.foreground.alpha = 50
                binding.Address.isEnabled = false
                binding.AddButton.isEnabled = false
                binding.Description.isEnabled = false
                binding.ExitButton.isEnabled = false
                binding.AddPhotoButton.isEnabled = false
                showPopup()
            }
        }
        intent.getStringExtra(AddPlacemarkDialog.EXTRA_ADDRESS)?.let {
            binding.Address.text.append(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialog() {
        isDialogShowing = true
        val rootLayout: ViewGroup = findViewById(R.id.root_layout)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.take_photo_dialog, null)
        val popupWindow = PopupWindow(view)

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT

        val camera: FrameLayout = view.findViewById(R.id.camera_btn)
        val gallery: FrameLayout = view.findViewById(R.id.gallery_btn)

        camera.setOnClickListener {
            tempImageUri = FileProvider.getUriForFile(
                baseContext,
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

        gallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        popupWindow.showAsDropDown(findViewById(R.id.AddPhotoButton))

        rootLayout.setOnClickListener {
            popupWindow.dismiss()
            isDialogShowing = false
        }
    }

    private fun showPopup() {
        val rootLayout: ViewGroup = findViewById(R.id.root_layout)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.create_complaint_popup_menu, null)
        val popupWindow = PopupWindow(view)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = size.x - 70

        TransitionManager.beginDelayedTransition(rootLayout)
        popupWindow.isOutsideTouchable = false
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)
        val publishButton: Button = view.findViewById(R.id.PublishButton) // опубликовать
        val closeButton = view.findViewById<ImageButton>(R.id.closePopup) // нажатие на крестик
        val radioAnon = view.findViewById<RadioButton>(R.id.Anon)
        val radioNeAnon = view.findViewById<RadioButton>(R.id.NeAnon)

        publishButton.setOnClickListener {
            if (!(radioAnon.isChecked) && !(radioNeAnon.isChecked))
                Toast.makeText(
                    applicationContext,
                    "Выберите тип публикации",
                    Toast.LENGTH_SHORT
                ).show()
            else
                if((radioNeAnon.isChecked) && (getCurrentUser() == null))
                    Toast.makeText(applicationContext, "Требуется авторизация", Toast.LENGTH_SHORT).show()
                else
                {
                   setComplaint(
                       Complaint(
                           category = binding.Spinner.selectedItem.toString(),
                           description = binding.Description.text.toString(),
                           address = binding.Address.text.toString(),
                           creation_day = "",
                           creator = if(radioAnon.isChecked) null else UserManager.getCurrentUser()?.uid
                       )
                   )
                    val complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
                    complaintViewModel.setTempImageFilePath(tempImageFilePath)
                    Toast.makeText(applicationContext, complaintViewModel.sendPhoto(), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ComplaintActivity::class.java)
                    intent.putExtra("FragmentMode", "Publish")
                    startActivity(intent)
            }
        }

        closeButton.setOnClickListener {
            popupWindow.dismiss()
            binding.RootFrame.foreground.alpha = 0
            binding.ExitButton.isEnabled = true
            binding.AddButton.isEnabled = true
            binding.Address.isEnabled = true
            binding.Description.isEnabled = true
            binding.AddPhotoButton.isEnabled = true
        }
    }

    private fun createImageFile(): File {
        val storageDir = applicationContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }
}
