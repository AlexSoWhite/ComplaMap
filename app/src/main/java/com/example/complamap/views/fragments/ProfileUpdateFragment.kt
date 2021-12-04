package com.example.complamap.views.fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.FragmentAuthorizedUserBinding
import com.example.complamap.databinding.FragmentProfileUpdateButtonsBinding
import com.example.complamap.model.TakePhotoContract
import com.example.complamap.viewmodel.ProfileViewModel

class ProfileUpdateFragment : Fragment() {

    companion object {
        private lateinit var profileViewModel: ProfileViewModel
        private lateinit var rootBinding: FragmentAuthorizedUserBinding

        fun newInstance(
            profileViewModel: ProfileViewModel,
            rootBinding: FragmentAuthorizedUserBinding
        ): ProfileUpdateFragment {
            this.profileViewModel = profileViewModel
            this.rootBinding = rootBinding

            return ProfileUpdateFragment()
        }
    }

    private lateinit var binding: FragmentProfileUpdateButtonsBinding
    private var uri: Uri? = null
    private val takePhotoLauncher =
        registerForActivityResult(TakePhotoContract()) {
            uri = it
            if (it.toString() != "null") {
                rootBinding.profilePic.setImageURI(it)
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
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
            rootBinding.profilePhotoContainer.setOnClickListener {
                takePhotoLauncher.launch(null)
            }
            binding.updateProfile.visibility = View.INVISIBLE
            rootBinding.logOut.visibility = View.INVISIBLE
            binding.confirmButton.visibility = View.VISIBLE
            binding.confirmButton.setOnClickListener {
                profileViewModel.updateUser(
                    uri,
                    rootBinding.username.text.toString()
                ) {
                    Toast.makeText(
                        activity,
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.confirmButton.visibility = View.INVISIBLE
                    binding.updateProfile.visibility = View.VISIBLE
                    rootBinding.logOut.visibility = View.VISIBLE
                    rootBinding.username.background.alpha = 0
                    rootBinding.editProfilePhoto.visibility = View.INVISIBLE
                    rootBinding.username.isFocusable = false
                    rootBinding.username.isFocusableInTouchMode = false
                    rootBinding.username.isCursorVisible = false
                }
            }
        }
    }
}
