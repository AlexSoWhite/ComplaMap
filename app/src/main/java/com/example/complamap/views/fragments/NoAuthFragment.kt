package com.example.complamap.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.views.activities.SignInActivity
import com.example.complamap.databinding.FragmentNoAuthBinding

class NoAuthFragment : Fragment() {
    private lateinit var binding: FragmentNoAuthBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoAuthBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textButton.setOnClickListener{
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
