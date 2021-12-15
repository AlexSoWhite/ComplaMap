package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.R
import com.example.complamap.databinding.FragmentSaveBinding
import com.example.complamap.views.activities.ComplaintActivity

class SaveFragment : Fragment(R.layout.fragment_save) {
    private lateinit var binding: FragmentSaveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.save.setOnClickListener {
            (activity as ComplaintActivity).edit()
        }
    }
}
