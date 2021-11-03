package com.example.complamap.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.complamap.Complaint
import com.example.complamap.R
import com.example.complamap.databinding.TestActivityComplaintBinding
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
            this, R.layout.test_activity_complaint
        )
        binding.complaint = Complaint()
            GlobalScope.launch {
                getData()
            }
        binding.ExitButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun get(): DocumentSnapshot {
        val complaintId: String? = intent.getStringExtra("ComplaintId")
        val docRef = db.collection("complaint").document(complaintId as String)
        val querySnapshot = docRef.get().await()
        return querySnapshot
    }

    private suspend fun getData() {
        try {
            val data = get().data?.get("status")
            val comp = get().toObject(Complaint::class.java)
            val date = comp?.creation_date?.toDate()
            var creator: String? = null

            comp?.creator?.get()?.addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    creator = task.result?.data?.get("user_name").toString()
                    Log.d(TAG, "User is found $creator" )
                }
                else
                    Log.d(TAG, "Sorry, not found" )
            }
            //creator doesn't work
            binding.creator=creator
            binding.complaint = comp
            Log.d(TAG, "DocumentSnapshot data: $data \n complaint: $comp \n date: $date" )
        } catch (exception: Exception) {
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    companion object {
        private val TAG = "QueryExample"
    }
}