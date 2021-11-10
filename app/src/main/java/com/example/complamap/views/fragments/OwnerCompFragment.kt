package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentOwnerComplBarBinding

class OwnerCompFragment : Fragment() {
    private lateinit var binding: FragmentOwnerComplBarBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOwnerComplBarBinding.inflate(inflater)
        return binding.root
    }
}
