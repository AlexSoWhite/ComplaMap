package com.example.complamap.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.complamap.R
import com.example.complamap.databinding.CreateComplaintPopUpBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.UserManager

class CreateComplaintPopUpActivity : AppCompatActivity() {

    private lateinit var binding: CreateComplaintPopUpBinding

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.create_complaint_pop_up)

        binding.PublishButton.setOnClickListener {
            if (!(binding.Anon.isChecked) && !(binding.NeAnon.isChecked)) {
                Toast.makeText(
                    applicationContext,
                    "Выберите тип публикации",
                    Toast.LENGTH_SHORT
                ).show()
            } else if ((binding.NeAnon.isChecked) && (UserManager.getCurrentUser() == null)) {
                Toast.makeText(
                    applicationContext,
                    "Требуется авторизация",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                ComplaintManager.getCurrentComplaint()?.creator =
                    if (binding.Anon.isChecked) null
                    else UserManager.getCurrentUser()?.uid
                val preview = Intent(this, ComplaintActivity::class.java)
                preview.putExtra("FragmentMode", "Publish")
                if (intent.hasExtra("uri")) {
                    preview.putExtra("uri", intent.getStringExtra("uri"))
                } else {
                    preview.putExtra("noPhoto", true)
                }
                resultLauncher.launch(preview)
            }
        }
    }
}
