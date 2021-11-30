package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentAuthorizedUserBinding
import com.example.complamap.model.ContextContainer
import com.example.complamap.model.UserManager
import com.example.complamap.viewmodel.ProfileViewModel

class AuthorizedUserFragment : Fragment(R.layout.fragment_authorized_user) {

    private lateinit var binding: FragmentAuthorizedUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizedUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.getUser { user ->
            binding.user = user
        }

        binding.username.background.alpha = 0

        binding.logOut.setOnClickListener {
            profileViewModel.deleteUserFromCache()
            profileViewModel.setUser(null)
            parentFragmentManager.commit {
                replace(R.id.profile_container, NoAuthFragment())
            }
            UserManager.setAuthorized(false)
        }

        profileViewModel.loadPhotoFromServer(
            ContextContainer.getContext(),
            binding.user!!,
            binding.profilePic
        )

        childFragmentManager.commit {
            replace(
                R.id.profile_content_container,
                ProfileUpdateFragment.newInstance(profileViewModel, binding)
            )
        }
    }
}
