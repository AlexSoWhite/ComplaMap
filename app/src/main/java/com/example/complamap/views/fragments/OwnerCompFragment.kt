package com.example.complamap.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentOwnerComplBarBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OwnerCompFragment : Fragment() {
    private lateinit var binding: FragmentOwnerComplBarBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOwnerComplBarBinding.inflate(inflater)
        var complaintId: String? = null
        if (activity?.intent?.getStringExtra("ComplaintId") != null) complaintId =
            activity?.intent?.getStringExtra("ComplaintId")!!
        binding.delete.setOnClickListener {
            if (complaintId != null) {
                GlobalScope.launch {
                    delete(complaintId)
                }
            }
        }
        return binding.root
    }

    private fun delete(id: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("complaint").document(id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    companion object {
        private const val TAG = "DeletingExample"
    }
}
