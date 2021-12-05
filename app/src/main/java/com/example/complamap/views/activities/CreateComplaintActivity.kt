package com.example.complamap.views.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.CreateComplaintActivityBinding
import com.example.complamap.model.Category
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.TakePhotoContract
import com.example.complamap.model.UserManager
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.views.fragments.AddPlacemarkDialog

class CreateComplaintActivity : AppCompatActivity() {
    companion object {
        val categories = mutableListOf<String>()
    }

    // uri for setting image content by setImageUri
    private var tempImageUri: Uri? = null

    private lateinit var complaintViewModel: ComplaintViewModel

    private lateinit var binding: CreateComplaintActivityBinding

    private val takePhotoLauncher =
        registerForActivityResult(TakePhotoContract()) {
            tempImageUri = it
            if (it.toString() != "null") {
                binding.Image.setImageURI(it)
                binding.deleteImage.visibility = View.VISIBLE
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
        binding.ExitButton.setOnClickListener {
            finish()
        }

        binding.AddPhotoButton.setOnClickListener {
            takePhotoLauncher.launch("")
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
        val rootLayout: ViewGroup = findViewById(R.id.root_layout)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.create_complaint_popup_menu, null)
        val popupWindow = PopupWindow(view)

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT

        TransitionManager.beginDelayedTransition(rootLayout)
        popupWindow.isOutsideTouchable = false
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            binding.RootFrame.foreground.alpha = 0
            binding.ExitButton.isEnabled = true
            binding.AddButton.isEnabled = true
            binding.Address.isEnabled = true
            binding.Description.isEnabled = true
            binding.AddPhotoButton.isEnabled = true
        }
        val publishButton: Button = view.findViewById(R.id.PublishButton) // опубликовать
        val closeButton = view.findViewById<ImageButton>(R.id.closePopup) // нажатие на крестик
        val radioAnon = view.findViewById<RadioButton>(R.id.Anon)
        val radioNeAnon = view.findViewById<RadioButton>(R.id.NeAnon)

        publishButton.setOnClickListener {
            if (!(radioAnon.isChecked) && !(radioNeAnon.isChecked)) {
                Toast.makeText(
                    applicationContext,
                    "Выберите тип публикации",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if ((radioNeAnon.isChecked) && (UserManager.getCurrentUser() == null)) {
                    Toast.makeText(
                        applicationContext,
                        "Требуется авторизация",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    ComplaintManager.setComplaint(
                        Complaint(
                            category = binding.Spinner.selectedItem.toString(),
                            description = binding.Description.text.toString(),
                            address = binding.Address.text.toString(),
                            creation_day = "",
                            status = "Принята",
                            followers = mutableListOf(),
                            approvals = 0,
                            approvers = mutableListOf(),
                            rejections = 0,
                            rejecters = mutableListOf(),
                            creator = if (radioAnon.isChecked) null
                            else UserManager.getCurrentUser()?.uid
                        )
                    )
                    val intent = Intent(this, ComplaintActivity::class.java)
                    intent.putExtra("FragmentMode", "Publish")
                    if (tempImageUri != null) {
                        intent.putExtra("uri", tempImageUri.toString())
                    } else {
                        intent.putExtra("noPhoto", true)
                    }
                    startActivity(intent)
                    popupWindow.dismiss()
                }
            }
        }

        closeButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }
}
