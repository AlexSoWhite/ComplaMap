package com.example.complamap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.NoAuthFragmentBinding

class NoAuthFragment: Fragment() {
    private lateinit var binding: NoAuthFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NoAuthFragmentBinding.inflate(inflater)
        return binding.root
    }
}
