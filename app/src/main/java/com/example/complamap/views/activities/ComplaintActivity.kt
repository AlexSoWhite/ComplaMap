package com.example.complamap.views.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.complamap.Complaint
import com.example.complamap.R
import com.example.complamap.User
import com.example.complamap.databinding.TestActivityComplaintBinding
import com.example.complamap.fragments.OwnerCompFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ComplaintActivity : AppCompatActivity() {

    private lateinit var binding: TestActivityComplaintBinding
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.test_activity_complaint
        )
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(binding.container.id, OwnerCompFragment())
//                .commit()
//        }
        binding.complaint = Complaint()
        GlobalScope.launch {
            getData()
        }
        binding.ExitButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun get(): DocumentSnapshot {
        val docRef = db.collection("complaint").document("E9tuewejxnMoizuxNp8p")
        val querySnapshot = docRef.get().await()
        return querySnapshot
    }

    private suspend fun getData() {
        var creator: User? = null
        try {
            val data = get().data?.get("status")
            val comp = get().toObject(Complaint::class.java)
            comp?.creator?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        creator = task.result?.toObject(User::class.java)
                        binding.creator = creator?.username
                        Log.d(TAG, "User is found $creator")
                    } catch (exception: Exception) {
                        Log.d(TAG, "User is a great error")
                    }
                } else
                    Log.d(TAG, "Sorry, not found")
            }
            binding.complaint = comp
            Log.d(TAG, "DocumentSnapshot data: $data \n complaint: $comp \n creator: $creator")
        } catch (exception: Exception) {
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    companion object {
        private val TAG = "QueryExample"
    }
}
