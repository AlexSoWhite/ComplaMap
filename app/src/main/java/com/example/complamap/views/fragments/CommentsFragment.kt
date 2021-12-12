package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.complamap.R
import com.example.complamap.databinding.CommentsFragmentBinding
import com.example.complamap.model.Comment
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.User
import com.example.complamap.viewmodel.CommentViewModel
import com.google.firebase.Timestamp

class CommentsFragment : Fragment(R.layout.comments_fragment) {

    private lateinit var binding: CommentsFragmentBinding
    private val currentComplaint = ComplaintManager.getCurrentComplaint()
    private lateinit var commentViewModel: CommentViewModel

    companion object {
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
    ): View {
        binding = CommentsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO забирать комменты из бд
        commentViewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        updateComments(currentComplaint!!.compId!!)
        binding.addCommentButton.setOnClickListener {
            if (binding.commentEditText.length() != 0) {
                val currentComment = Comment(
                    complaint?.compId,
                    authorId = user?.uid,
                    comment_text = binding.commentEditText.text.toString(),
                    date = android.text.format.DateFormat.format(
                        "dd.MM.yyyy",
                        Timestamp.now().toDate()
                    ).toString()
                )
                commentViewModel.addComment(currentComplaint.compId!!, currentComment)
            } else {
                Toast.makeText(
                    context,
                    "Ведите комментарий",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.commentEditText.text = null
            updateComments(currentComplaint.compId!!)
        }
        binding.commentsRecycler.layoutManager = LinearLayoutManager(this.context)
    }

    private fun updateComments(complaintId: String) {
        commentViewModel.getComments(complaintId) { list ->
            binding.commentsRecycler.adapter = CommentAdapter(list, commentViewModel)
        }
    }
}
