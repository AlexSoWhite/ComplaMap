package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.complamap.R
import com.example.complamap.databinding.FragmentAuthorizedUserBinding
import com.example.complamap.model.UserManager

class AuthorizedUserFragment : Fragment(R.layout.fragment_authorized_user) {

    private lateinit var binding: FragmentAuthorizedUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizedUserBinding.inflate(layoutInflater)
        binding.logOut.setOnClickListener {
            UserManager.deleteUserFromCache()
            UserManager.setUser(null)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = UserManager.getCurrentUser()
        binding.user = user
    }
}
