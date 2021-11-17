package com.example.complamap.views.activities

import android.location.Geocoder
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.databinding.ActivityComplaintBinding
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.User
import com.example.complamap.viewmodel.ProfileViewModel
import com.example.complamap.views.fragments.OwnerCompFragment
import com.example.complamap.views.fragments.PublishFragment
import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.firestore.DocumentSnapshot
// import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
// import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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





    private suspend fun getData() {
        var creator: User?
        val comp: Complaint?
        val locale = Locale("ru", "RU")
        val geocoder = Geocoder(this, locale)

            comp = ComplaintManager.getCurrentComplaint()
            if (comp != null) {
                withContext(Dispatchers.Main) {
                    Glide.with(applicationContext).load(comp.photo).into(binding.photo)
                }

                creator = if(intent.extras?.getBoolean("isAnon")!!) // тоже перенести во вьюмодель
                    null
                else
                    null //TODO
                    //FirebaseFirestore.getInstance().collection("users").document(ComplaintManager.getCurrentComplaint()?.uid) as User?
                binding.creator = creator

                binding.complaint = comp
            }

    }

    companion object {
        private const val TAG = "QueryExample"
    }
}
