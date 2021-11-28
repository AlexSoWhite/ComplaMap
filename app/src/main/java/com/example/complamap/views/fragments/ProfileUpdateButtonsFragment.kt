package com.example.complamap.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.complamap.R
import com.example.complamap.databinding.FragmentAuthorizedUserBinding
import com.example.complamap.databinding.FragmentProfileUpdateButtonsBinding
import com.example.complamap.viewmodel.ProfileViewModel

class ProfileUpdateButtonsFragment(
    private val profileViewModel: ProfileViewModel,
    private val rootBinding: FragmentAuthorizedUserBinding
    )
    : Fragment(R.layout.fragment_profile_update_buttons) {

    private lateinit var binding: FragmentProfileUpdateButtonsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentProfileUpdateButtonsBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.updateProfile.setOnClickListener {
            rootBinding.username.background.alpha = 100
            rootBinding.editProfilePhoto.visibility = View.VISIBLE
            rootBinding.username.isFocusable = true
            rootBinding.username.isFocusableInTouchMode = true
            rootBinding.username.isCursorVisible = true
            rootBinding.editProfilePhoto.setOnClickListener {

            }
        }
    }
}