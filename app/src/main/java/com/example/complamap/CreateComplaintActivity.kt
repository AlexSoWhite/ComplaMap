package com.example.complamap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.complamap.databinding.CreateComplaintActivityBinding
import android.content.Context
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow


class CreateComplaintActivity : AppCompatActivity() {
    private lateinit var binding: CreateComplaintActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setContentView(R.layout.create_complaint_activity)
        val addButton: ImageButton = findViewById(R.id.AddButton) // кнопка внизу
        addButton.setOnClickListener { // обработка нажатия на кнопку внизу
            showPopup()
        }
        val exitButton: ImageButton = findViewById(R.id.ExitButton)
        exitButton.setOnClickListener{
            //TODO destroy createcomplaintactivity
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

        val closeButton = view.findViewById<ImageButton>(R.id.closePopup) // нажатие на крестик
        closeButton.setOnClickListener {
            popupWindow.dismiss()
        }
    }
}

