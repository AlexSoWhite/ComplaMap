package com.example.complamap.views.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.complamap.R
import com.example.complamap.databinding.FragmentViewerComplBarBinding
import com.example.complamap.model.Comment
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
    private var rating: Double? = null
    private var isFollowing = false
    private var isAppr = false
    private var isRej = false
    private var creatorId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewerComplBarBinding.inflate(inflater)
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.getUser { currentUser = it }
        isAppr = currentUser?.uid?.let { currentComplaint?.approvers?.contains(it) } == true
        isRej = currentUser?.uid?.let { currentComplaint?.rejecters?.contains(it) } == true
        if (currentUser?.uid?.let { currentComplaint?.followers?.contains(it) } == true) {
            isFollowing = true
            binding.btnText.text = "отписаться"
        }
        binding.approve.isEnabled = !isRej
        binding.reject.isEnabled = !isAppr
        binding.complaint = currentComplaint
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        creatorId = arguments?.getString("creator").toString()

        binding.follow.setOnClickListener {
            if (!isFollowing) {
                currentUser?.uid?.let { it1 ->
                    if (compId != null) {
                        currentUser!!.subs?.add(compId)
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
                        complaintViewModel.removeFollowers(currentComplaint.compId!!, it1)
                    }
                }
                isFollowing = false
                binding.btnText.text = "отслеживать"
            }
            profileViewModel.deleteUserFromCache()
            currentUser?.let { it1 -> profileViewModel.putUserToCache(it1) }
        }

        binding.approve.setOnClickListener {
            if (isAppr) {
                currentComplaint?.approvals = currentComplaint?.approvals?.minus(1)
                isAppr = false
                binding.reject.isEnabled = true
            } else {
                currentComplaint?.approvals = currentComplaint?.approvals?.plus(1)
                isAppr = true
                binding.reject.isEnabled = false
            }
            binding.apprNum.text = currentComplaint?.approvals.toString()
            binding.approve.isEnabled = true
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
            editRating(isAppr)
        }
        binding.reject.setOnClickListener {
            if (isRej) {
                currentComplaint?.rejections = currentComplaint?.rejections?.plus(1)
                binding.approve.isEnabled = true
                isRej = false
            } else {
                currentComplaint?.rejections = currentComplaint?.rejections?.minus(1)
                binding.approve.isEnabled = false
                isRej = true
            }
            binding.rejNum.text = currentComplaint?.rejections.toString()
            binding.reject.isEnabled = true
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
            editRating(!isRej)
        }
    }

    private fun editRating(flag: Boolean) {
        if (creatorId != "null") {
            Log.d("This", "STRART WITH $creatorId")
            profileViewModel.getUserFromDatabase(creatorId) { user ->
                rating = if (flag) {
                    user?.rating?.plus(0.2)
                } else {
                    user?.rating?.minus(0.2)
                }
                profileViewModel.editRating(creatorId, rating!!)
            }
        }

        val testComment: Comment = Comment(
            currentComplaint,
            currentUser,
            "sadasdsa",
            "date",
            currentUser!!.username!!,
            currentUser!!.rating!!.toString(),

        )

        if(currentComplaint!!.comments.isEmpty())
            Toast.makeText(context, "ПУсто", Toast.LENGTH_SHORT).show()
       // currentComplaint!!.comments.add(testComment)


        binding.commentsRecycler.adapter = CommentAdapter(currentComplaint!!.comments)
        binding.commentsRecycler.layoutManager = LinearLayoutManager(context)

        binding.comment.setOnClickListener {

            childFragmentManager.beginTransaction()
                .replace(R.id.commentsContainer, CommentAddFragment.getInstance(currentUser!!, currentComplaint!!))
                .commit()
                    }
    }
}
