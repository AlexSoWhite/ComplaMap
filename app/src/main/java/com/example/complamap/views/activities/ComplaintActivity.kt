package com.example.complamap.views.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivityComplaintBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.views.fragments.PublishFragment

class ComplaintActivity : AppCompatActivity() {

    private lateinit var complaintViewModel: ComplaintViewModel

    private lateinit var binding: ActivityComplaintBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_complaint
        )
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
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
                complaintViewModel.loadPhotoFromServer(
                    baseContext,
                    ComplaintManager.getCurrentComplaint()!!,
                    binding.photo
                )
            }
        }

        binding.complaint = ComplaintManager.getCurrentComplaint()

        if (ComplaintManager.getCurrentComplaint()!!.creator != null) {
            complaintViewModel.loadUserData(ComplaintManager.getCurrentComplaint()!!.creator!!) {
                binding.creator = it
            }
        }

        binding.ExitButton.setOnClickListener {
            finish()
        }
    }
}
