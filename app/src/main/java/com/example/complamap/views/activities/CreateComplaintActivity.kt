package com.example.complamap.views.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivityCreateComplaintBinding
import com.example.complamap.model.Category
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.CreateComplaintDialogContract
import com.example.complamap.model.TakePhotoContract
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.views.fragments.AddPlacemarkDialog
import com.google.firebase.firestore.GeoPoint

class CreateComplaintActivity : AppCompatActivity() {
    companion object {
        val categories = mutableListOf<String>()
    }

    // uri for setting image content by setImageUri
    private var tempImageUri: Uri? = null

    private lateinit var complaintViewModel: ComplaintViewModel

    private lateinit var binding: ActivityCreateComplaintBinding

    private val takePhotoLauncher =
        registerForActivityResult(TakePhotoContract()) {
            tempImageUri = it
            if (it.toString() != "null") {
                binding.Image.setImageURI(it)
                binding.deleteImage.visibility = View.VISIBLE
            }
        }

    private val dialogLauncher =
        registerForActivityResult(CreateComplaintDialogContract()) {
            if (it != null) {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityCreateComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
        binding.ExitButton.setOnClickListener {
            finish()
        }

        binding.AddPhotoButton.setOnClickListener {
            takePhotoLauncher.launch("")
        }

        binding.AddButton.setOnClickListener { // обработка нажатия на кнопку внизу
            if (binding.Address.text.isEmpty() || binding.Address.text.isBlank()) {
                Toast.makeText(applicationContext, "Введите адрес", Toast.LENGTH_SHORT).show()
            } else if (binding.Description.text.isEmpty() || binding.Description.text.isBlank()) {
                Toast.makeText(applicationContext, "Введите описание", Toast.LENGTH_SHORT).show()
            } else {
                showPopup()
            }
        }

        intent.getStringExtra(AddPlacemarkDialog.EXTRA_ADDRESS)?.let {
            binding.Address.text.append(it)
        }

        binding.deleteImage.setOnClickListener {
            tempImageUri = null
            binding.Image.setImageResource(R.drawable.default_placeholder)
            binding.deleteImage.visibility = View.INVISIBLE
        }

        for (it in Category.values()) {
            categories.add(it.category)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.Spinner.setSelection(0)
        binding.Spinner.adapter = adapter
    }

    private fun showPopup() {

        ComplaintManager.setComplaint(
            Complaint(
                category = binding.Spinner.selectedItem.toString(),
                description = binding.Description.text.toString(),
                address = binding.Address.text.toString(),
                location = GeoPoint(
                    intent.getDoubleExtra(AddPlacemarkDialog.EXTRA_LATITUDE, 0.0),
                    intent.getDoubleExtra(AddPlacemarkDialog.EXTRA_LONGITUDE, 0.0)
                ),
                creationDay = "",
                status = "Принята",
                followers = mutableListOf(),
                approvals = 0,
                approvers = mutableListOf(),
                rejections = 0,
                rejecters = mutableListOf()
            )
        )
        if (tempImageUri != null) {
            dialogLauncher.launch(tempImageUri.toString())
        } else {
            dialogLauncher.launch(null)
        }
    }
}
