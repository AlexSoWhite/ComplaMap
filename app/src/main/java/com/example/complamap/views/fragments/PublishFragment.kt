package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentPublishBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.viewmodel.ComplaintViewModel

class PublishFragment : Fragment(R.layout.fragment_publish){
    private lateinit var binding: FragmentPublishBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPublishBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var Confirmed: Boolean = false

        binding.Confirm.setOnClickListener {
            if (!Confirmed) {
                val complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
                complaintViewModel.putComplaintToDatabase(ComplaintManager.getCurrentComplaint()!!)
                Confirmed = true
            } else
                Toast.makeText(context, "Жалоба уже опубликована", Toast.LENGTH_SHORT).show()
        }
    }
}