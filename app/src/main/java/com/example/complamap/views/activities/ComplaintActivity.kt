package com.example.complamap.views.activities

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivityComplaintBinding
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.viewmodel.ProfileViewModel
import com.example.complamap.views.fragments.OwnerCompFragment
import com.example.complamap.views.fragments.PublishFragment
import com.example.complamap.views.fragments.SaveFragment
import java.io.File
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.example.complamap.model.UserRepository
import kotlinx.coroutines.launch


class ComplaintActivity : AppCompatActivity() {
    companion object {
        var categories = listOf(
            "Транспорт",
            "Категория 1",
            "Категория 2",
            "Общество",
            "Другое",
            "Категория длинная очень очень"
        )
    }

    private lateinit var complaintViewModel: ComplaintViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var binding: ActivityComplaintBinding
    private var currentUser: String? = ""
    private var creator: String = ""
    private var currentComplaint: Complaint? = null
    var imageUri: Uri? = null
    var imageFilePath = ""
    private var isDialogShowing: Boolean = false

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
                }
                else -> {
                    // do something if permission for camera denied
                    requestPermissions(Array(1) { CAMERA_SERVICE }, 0)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.photo.setImageURI(imageUri)
                showDialog()
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            binding.photo.setImageURI(it)
            imageUri = it
            val file = createImageFile()
            imageFilePath = file.absolutePath
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_complaint
        )
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.getUser { user ->
            currentUser = user?.uid
        }
        creator = ComplaintManager.getCurrentComplaint()?.creator.toString()
        currentComplaint = ComplaintManager.getCurrentComplaint()
        when (intent.getStringExtra("FragmentMode")) {

            "Publish" -> {
                var uri: Uri? = null
                var path: String? = null
                if (!intent.hasExtra("noPhoto")) {
                    uri = intent.getStringExtra("uri")!!.toUri()
                    path = intent.getStringExtra("path")!!
                }
                complaintViewModel.loadPhotoFromUri(
                    baseContext,
                    uri,
                    binding.photo
                )
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, PublishFragment(uri, path))
                    .commit()
            }

            "View" -> {
                if (currentUser != "") {
                    if (currentUser == creator) {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.container.id, OwnerCompFragment())
                            .commit()
                    }
                }

                complaintViewModel.loadPhotoFromServer(
                    baseContext,
                    currentComplaint!!,
                    binding.photo
                )

            }
        }


        binding.complaint = currentComplaint
        lifecycleScope.launch {
            binding.creator = UserRepository.getUserFromDatabase(creator)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.category.adapter = adapter
        val pos = categories.indexOf(currentComplaint?.category)
        pos.let { binding.category.setSelection(it) }
        binding.ExitButton.setOnClickListener {
            exit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun makeEditable() {
        binding.description.isFocusable = true
        binding.description.isFocusableInTouchMode = true
        binding.description.isCursorVisible = true
        binding.categoryTextView.visibility = View.INVISIBLE
        binding.category.visibility = View.VISIBLE

        if (currentComplaint?.status != "В работе") {
            binding.address.isFocusable = true
            binding.address.isFocusableInTouchMode = true
            binding.address.isCursorVisible = true
        }

        binding.photo.setOnClickListener {
            if (!isDialogShowing) {
                showDialog()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, SaveFragment())
            .commit()
    }

    fun edit() {
        ComplaintManager.getCurrentComplaint()?.compId?.let {
            complaintViewModel.editComplaint(
                it, binding.description.text.toString(), binding.category.selectedItem.toString(),
                binding.address.text.toString(), imageUri, imageFilePath
            )
        }
        Toast.makeText(this, "Изменено", Toast.LENGTH_SHORT).show()
        binding.categoryTextView.text = binding.category.selectedItem.toString()
        binding.categoryTextView.visibility = View.VISIBLE
        binding.category.visibility = View.INVISIBLE
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, OwnerCompFragment())
            .commit()
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
        popupWindow.setBackgroundDrawable(getDrawable(R.drawable.black_border))

        val camera: FrameLayout = view.findViewById(R.id.camera_btn)
        val gallery: FrameLayout = view.findViewById(R.id.gallery_btn)

        camera.setOnClickListener {
            imageUri = FileProvider.getUriForFile(
                baseContext,
                "com.example.complamap.provider",
                createImageFile().also {
                    imageFilePath = it.absolutePath
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
        popupWindow.showAtLocation(findViewById(R.id.photo), Gravity.TOP, 0, 1100)

        rootLayout.setOnClickListener {
            popupWindow.dismiss()
            isDialogShowing = false
        }
    }

    private fun createImageFile(): File {
        val storageDir = applicationContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

    fun exit(){
        finish()
    }
}
