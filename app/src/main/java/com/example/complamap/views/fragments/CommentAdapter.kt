package com.example.complamap.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.CommentBinding
import com.example.complamap.model.Comment
import com.example.complamap.model.ContextContainer
import com.example.complamap.viewmodel.CommentViewModel
import com.example.complamap.views.activities.ComplaintActivity

class CommentAdapter (
    private val comments: List<Comment>?,
    private val commentViewModel: CommentViewModel
    ): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {



    class CommentViewHolder(
        itemView: View,
        private val commentViewModel: CommentViewModel
    ): RecyclerView.ViewHolder(itemView) {

        val binding = CommentBinding.bind(itemView)


        var profilePicture: ImageView? = null
        var userName: TextView? = null
        var rating: TextView? = null
        var date: TextView? = null
        var commentText: TextView? = null

        init {
            profilePicture = itemView.findViewById(R.id.profilePicture)
            userName = itemView.findViewById(R.id.userName)
            rating = itemView.findViewById(R.id.userRating)
            date = itemView.findViewById(R.id.creationDate)
            commentText = itemView.findViewById(R.id.commentText)
        }

        fun bind(comment: Comment){
            commentViewModel.loadAuthorProfilePic(
                ContextContainer.getContext(),
                comment,
                binding.profilePicture
            )
            binding.comment = comment
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): CommentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
            .inflate(R.layout.comment, parent, false)
        return CommentViewHolder(itemView, commentViewModel)
    }

    override fun getItemCount(): Int {
        return comments!!.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments!![position])
    }
}