package com.example.complamap.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentViewerComplBarBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.User
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.viewmodel.ProfileViewModel

class ViewerCompFragment : Fragment() {
    companion object {
        const val APPROVALS = "approvals"
        const val APPROVERS = "approvers"
        const val REJECTIONS = "rejections"
        const val REJECTERS = "rejecters"
    }

    private lateinit var binding: FragmentViewerComplBarBinding
    private lateinit var complaintViewModel: ComplaintViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private val currentComplaint = ComplaintManager.getCurrentComplaint()
    private val compId = currentComplaint?.compId
    private var currentUser: User? = null
    private var isFollowing = false
    private var isAppr = false
    private var isRej = false
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
        binding.complaint = currentComplaint
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAppr = currentUser?.uid?.let { currentComplaint?.approvers?.contains(it) } == true
        isRej = currentUser?.uid?.let { currentComplaint?.rejecters?.contains(it) } == true
        if (isAppr) {
            binding.reject.isClickable = false
        }
        if (isRej) {
            binding.approve.isClickable = false
        }
        binding.follow.setOnClickListener {
            if (!isFollowing) {
                currentUser?.uid?.let { it1 ->
                    if (compId != null) {
                        profileViewModel.addSubsToUser(it1, compId)
                    }
                    if (currentComplaint != null) {
                        complaintViewModel.addFollowers(currentComplaint.compId!!, it1)
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

        binding.approve.setOnClickListener {
            if (isAppr) {
                currentComplaint?.approvals = currentComplaint?.approvals?.minus(1)
                isAppr = false
                binding.reject.isClickable = true
            } else {
                currentComplaint?.approvals = currentComplaint?.approvals?.plus(1)
                isAppr = true
                binding.reject.isClickable = false
            }
            binding.apprNum.text = currentComplaint?.approvals.toString()
            binding.approve.isClickable = true
            if (compId != null) {
                currentComplaint?.approvals?.let { it1 ->
                    currentUser?.uid?.let { it2 ->
                        complaintViewModel.editVotes(
                            compId,
                            APPROVALS,
                            it1,
                            APPROVERS,
                            it2,
                            isAppr
                        )
                    }
                }
            }
        }
        binding.reject.setOnClickListener {
            if (isRej) {
                currentComplaint?.rejections = currentComplaint?.rejections?.plus(1)
                binding.approve.isClickable = true
                isRej = false
            } else {
                currentComplaint?.rejections = currentComplaint?.rejections?.minus(1)
                binding.approve.isClickable = false
                isRej = true
            }
            binding.rejNum.text = currentComplaint?.rejections.toString()
            binding.reject.isClickable = true
            if (compId != null) {
                currentComplaint?.rejections?.let { it1 ->
                    currentUser?.uid?.let { it2 ->
                        complaintViewModel.editVotes(
                            compId,
                            REJECTIONS,
                            it1,
                            REJECTERS,
                            it2,
                            isRej
                        )
                    }
                }
            }
        }
        binding.comment.setOnClickListener {

            childFragmentManager.beginTransaction()
                .replace(R.id.commentsContainer, CommentAddFragment.getInstance(currentUser!!, currentComplaint!!))
                .commit()

            childFragmentManager.commit {
                replace(R.id.commentsContainer, CommentAddFragment())
            }

        }
    }
}
