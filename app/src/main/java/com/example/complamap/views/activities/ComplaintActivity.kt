package com.example.complamap.views.activities

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.complamap.R
import com.example.complamap.databinding.ActivityComplaintBinding
import com.example.complamap.model.Complaint
import com.example.complamap.model.User
import com.example.complamap.views.fragments.OwnerCompFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ComplaintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintBinding
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_complaint
        )
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, OwnerCompFragment())
                .commit()
        }
        binding.complaint = Complaint()
        GlobalScope.launch {
            getData()
        }
        binding.ExitButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun get(): DocumentSnapshot {
        var complaintId = "dis37Cx4tyz8K5hZmf9p"
        if (intent.getStringExtra("ComplaintId") != null) complaintId =
            intent.getStringExtra("ComplaintId")!!
        val docRef = db.collection("complaint").document(complaintId)
        return docRef.get().await()
    }

    private suspend fun getData() {
        var creator: User?
        val comp: Complaint?
        val locale = Locale("ru", "RU")
        val geocoder = Geocoder(this, locale)
        try {
            comp = get().toObject(Complaint::class.java)
            if (comp != null) {
                withContext(Dispatchers.Main) {
                    Glide.with(applicationContext).load(comp.photo).into(binding.photo)
                }
                val address = comp.location?.let {
                    geocoder.getFromLocation(
                        it.latitude,
                        it.longitude,
                        1
                    )
                }
                if (address != null) {
                    comp.address = address[0].getAddressLine(0)
                }
                comp.creation_day = android.text.format.DateFormat.format(
                    "dd.MM.yyyy",
                    comp.creation_date?.toDate()
                ).toString()

                comp.creator?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        try {
                            creator = task.result?.toObject(User::class.java)
                            binding.creator = creator
                        } catch (exception: Exception) {
                            Log.d(TAG, "User is broken")
                        }
                    } else
                        Log.d(TAG, "Sorry, not found")
                }
                Log.d(TAG, "User: ${comp.creator}")
                binding.complaint = comp
            }
        } catch (exception: Exception) {
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    companion object {
        private const val TAG = "QueryExample"
    }
}
