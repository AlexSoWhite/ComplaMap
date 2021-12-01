package com.example.complamap.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.databinding.FragmentViewerComplBarBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.User
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.viewmodel.ProfileViewModel

class ViewerCompFragment : Fragment() {
    private lateinit var binding: FragmentViewerComplBarBinding
    private lateinit var complaintViewModel: ComplaintViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private val currentComplaint = ComplaintManager.getCurrentComplaint()
    private val compId = currentComplaint?.compId
    private var currentUser: User? = null
    private var isFollowing = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewerComplBarBinding.inflate(inflater)
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.getUser { currentUser = it }
        if (currentUser?.uid?.let { currentComplaint?.followers?.contains(it) } == true) {
            isFollowing = true
            binding.btnText.text = "отписаться"
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.follow.setOnClickListener {
            if (!isFollowing) {
                currentUser?.uid?.let { it1 ->
                    if (compId != null) {
                        profileViewModel.addSubsToUser(it1, compId)
                    }
                    if (currentComplaint != null) {
                        if (!currentComplaint.followers?.contains(it1)!!) {
                            complaintViewModel.addFollowers(currentComplaint.compId!!, it1)
                        }
                    }
                }
                isFollowing = true
                binding.btnText.text = "отписаться"
            } else {
                currentUser?.uid?.let { it1 ->
                    if (compId != null) {
                        profileViewModel.removeSubsFromUser(it1, compId)
                    }
                    if (currentComplaint != null) {
                        if (!currentComplaint.followers?.contains(it1)!!) {
                            complaintViewModel.removeFollowers(currentComplaint.compId!!, it1)
                        }
                    }
                }
                isFollowing = false
                binding.btnText.text = "отслеживать"
            }
        }
    }
}
