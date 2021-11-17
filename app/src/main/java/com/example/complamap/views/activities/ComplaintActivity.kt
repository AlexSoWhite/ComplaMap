package com.example.complamap.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.complamap.R
import com.example.complamap.databinding.ActivityComplaintBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.views.fragments.PublishFragment

class ComplaintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_complaint
        )
        when(intent.getStringExtra("FragmentMode")) {
            "Publish"-> {
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, PublishFragment())
                    .commit()
            }
        }
        binding.complaint = ComplaintManager.getCurrentComplaint()

        binding.ExitButton.setOnClickListener {
            finish()
        }

    }
}