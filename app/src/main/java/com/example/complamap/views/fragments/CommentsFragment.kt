package com.example.complamap.views.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.CommentsFragmentBinding
import com.example.complamap.databinding.FragmentAddCommentBinding
import com.example.complamap.model.Comment
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.User
import com.example.complamap.viewmodel.CommentViewModel
import com.google.firebase.Timestamp

class CommentsFragment: Fragment(R.layout.comments_fragment) {
    private lateinit var binding: CommentsFragmentBinding
    private val currentComplaint = ComplaintManager.getCurrentComplaint()
    private lateinit var commentViewModel: CommentViewModel

    companion object{
        private var user: User? = null
        private var complaint: Complaint? = null

        fun getInstance(user: User, complaint: Complaint): CommentsFragment {
            this.user = user
            this.complaint = complaint
            return CommentsFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CommentsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentViewModel = ViewModelProvider(this)[CommentViewModel::class.java]

        binding.addCommentButton.setOnClickListener {
            if(binding.commentEditText.length() != 0) {
                val currentComment = Comment(
                    currentComplaint,
                    user,
                    binding.commentEditText.text.toString(),
                    android.text.format.DateFormat.format(
                        "dd.MM.yyyy",
                        Timestamp.now()!!.toDate()
                    ).toString()
                )
                currentComplaint!!.comments.add(currentComment)
                Toast.makeText(context, complaint!!.comments.size.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
            else
                Toast.makeText(context, "Ведите комментарий", Toast.LENGTH_SHORT).show()

            binding.commentEditText.text = null
        }
        binding.commentsRecycler.adapter = CommentAdapter(currentComplaint!!.comments, commentViewModel)
        binding.commentsRecycler.layoutManager = LinearLayoutManager(this.context)
    }
}
