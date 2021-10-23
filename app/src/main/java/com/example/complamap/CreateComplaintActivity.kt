package com.example.complamap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import com.example.complamap.databinding.CreateComplaintActivityBinding
import com.example.complamap.databinding.FragmentPhotoBinding


class CreateComplaintActivity : AppCompatActivity() {

    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()){
           binding.Image.setImageURI(it)
    }

    private lateinit var binding: CreateComplaintActivityBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setContentView(R.layout.create_complaint_activity)
        val addButton: ImageButton = findViewById(R.id.AddButton) // кнопка внизу
        val address: EditText = findViewById(R.id.Address)
        addButton.setOnClickListener { // обработка нажатия на кнопку внизу
         address.isEnabled = false
         addButton.isEnabled=false
         showPopup()
        }
        val exitButton: ImageButton = findViewById(R.id.ExitButton)
        exitButton.setOnClickListener{
            onPause()
            onStop()
        }
        val addPhotoButton: ImageButton = findViewById(R.id.AddPhotoButton)
         addPhotoButton.setOnClickListener {
             openGallery.launch("image/*")
         }
    }

    private fun showPopup() {
        val rootLayout = findViewById<ViewGroup>(R.id.root_layout)
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.create_complaint_popup_menu, null)
        val popupWindow = PopupWindow(
            view,
            1000, 1100
        )
        TransitionManager.beginDelayedTransition(rootLayout)
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)

        val addButton: ImageButton = findViewById(R.id.AddButton) // кнопка внизу
        val address: EditText = findViewById(R.id.Address)        //я не дебил 2 раза их объявлять просто хз как еще
        


        val closeButton = view.findViewById<ImageButton>(R.id.closePopup) // нажатие на крестик
        closeButton.setOnClickListener {
            popupWindow.dismiss()
            address.isEnabled = true
            addButton.isEnabled=true
        }
    }
}

