package com.example.complamap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.complamap.databinding.CreateComplaintActivityBinding


class CreateComplaintActivity : AppCompatActivity() {



    private lateinit var binding: CreateComplaintActivityBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setContentView(R.layout.create_complaint_activity)
        val exitButton: ImageButton = findViewById(R.id.ExitButton)
        exitButton.setOnClickListener{
             finish()
         }
        val address: EditText = findViewById(R.id.Address)
        val description: EditText=findViewById(R.id.Description)
        val addPhotoButton: ImageButton = findViewById(R.id.AddPhotoButton)
        addPhotoButton.setOnClickListener {
             openGallery.launch("image/*")
         }
        val addButton: ImageButton = findViewById(R.id.AddButton) // кнопка внизу
        addButton.setOnClickListener { // обработка нажатия на кнопку внизу
            if ((address.length() == 0)||(description.length()==0)) {
                Toast.makeText(applicationContext, "Введите адрес и описание проблемы", Toast.LENGTH_SHORT).show()
            }
            else{
                address.isEnabled = false
                addButton.isEnabled = false
                exitButton.isEnabled = false
                addPhotoButton.isEnabled=false
                showPopup()
            }
        }

    }
    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()){
        val image: ImageView = findViewById(R.id.Image)
        image.setImageURI(it)
    }
    private fun showPopup() {
        val rootLayout: ViewGroup = findViewById(R.id.root_layout)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.create_complaint_popup_menu, null)
        val popupWindow = PopupWindow(
            view,
            1000,
            1100
        )
        TransitionManager.beginDelayedTransition(rootLayout)
        popupWindow.isOutsideTouchable = false
       //popupWindow.setBackgroundDrawable(ColorDrawable(123))
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)

        val addButton: ImageButton = findViewById(R.id.AddButton) // кнопка внизу
        val address: EditText = findViewById(R.id.Address)        //я не дебил 2 раза их объявлять просто хз как еще
        val exitButton: ImageButton = findViewById(R.id.ExitButton)
        val closeButton = view.findViewById<ImageButton>(R.id.closePopup) // нажатие на крестик
        val addPhotoButton: ImageButton = findViewById(R.id.AddPhotoButton)

        closeButton.setOnClickListener {
            popupWindow.dismiss()
            address.isEnabled = true
            addButton.isEnabled=true
            exitButton.isEnabled=true
            addPhotoButton.isEnabled=true

        }
    }
}

