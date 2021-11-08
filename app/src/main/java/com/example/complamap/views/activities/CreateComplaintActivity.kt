package com.example.complamap.views.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.complamap.databinding.CreateComplaintActivityBinding
import com.example.complamap.R
import com.google.firebase.firestore.FirebaseFirestore


class CreateComplaintActivity : AppCompatActivity() {
    private lateinit var binding: CreateComplaintActivityBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ExitButton.setOnClickListener{
             finish()
         }

        binding.AddPhotoButton.setOnClickListener {
             openGallery.launch("image/*")
         }

        binding.RootFrame.foreground.alpha = 0

        binding.AddButton.setOnClickListener { // обработка нажатия на кнопку внизу
            if (binding.Address.length() == 0) {
                Toast.makeText(applicationContext, "Введите адрес", Toast.LENGTH_SHORT).show()
            }
            else{
                binding.RootFrame.foreground.alpha=50
                binding.Address.isEnabled = false
                binding.AddButton.isEnabled = false
                binding.Description.isEnabled = false
                binding.ExitButton.isEnabled = false
                binding.AddPhotoButton.isEnabled=false
                showPopup()
            }
        }
     }
    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()){
        binding.Image.setImageURI(it)
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

      //  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // val slideIn = Slide()
          //  slideIn.slideEdge = Gravity.BOTTOM
           // popupWindow.enterTransition = slideIn
      //  }
        val addButton: ImageButton = findViewById(R.id.AddButton) // кнопка внизу
        val address: EditText = findViewById(R.id.Address)
        val exitButton: ImageButton = findViewById(R.id.ExitButton)
        val addPhotoButton: Button = findViewById(R.id.AddPhotoButton)
        val rootFrame: FrameLayout= findViewById(R.id.RootFrame)
        val description: EditText=findViewById(R.id.Description)
        val publishButton: Button = view.findViewById(R.id.PublishButton)//опубликовать
        val closeButton = view.findViewById<ImageButton>(R.id.closePopup) // нажатие на крестик



        publishButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            val intent = Intent(this, ComplaintActivity::class.java)
            startActivity(intent)
        }

        closeButton.setOnClickListener {
            popupWindow.dismiss()
            rootFrame.foreground.alpha = 0
            address.isEnabled = true
            addButton.isEnabled=true
            description.isEnabled = true
            exitButton.isEnabled=true
            addPhotoButton.isEnabled=true

        }
    }
}
