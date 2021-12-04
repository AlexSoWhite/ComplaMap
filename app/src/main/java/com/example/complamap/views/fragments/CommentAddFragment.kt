package com.example.complamap.views.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.FragmentAddCommentBinding
import com.example.complamap.model.Comment
import com.example.complamap.model.Complaint
import com.example.complamap.model.User

class CommentAddFragment: Fragment(R.layout.fragment_add_comment) {
    private lateinit var binding: FragmentAddCommentBinding

    companion object{
        private var user: User? = null
        private var complaint: Complaint? = null

        fun getInstance(user: User, complaint: Complaint): CommentAddFragment {
            this.user = user
            this.complaint = complaint
            return CommentAddFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCommentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler: RecyclerView
        binding.commentButton.setOnClickListener {
            var currentComment: Comment
            = Comment(
                complaint,
                user,
                binding.editText.text.toString(),
                null,
            )
        }
            complaint!!.comments?.toMutableList()?.add(Comment(complaint, user))
        }
    }
