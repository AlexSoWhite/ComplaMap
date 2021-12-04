package com.example.complamap.views.fragments

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.model.Comment

class CommentAdapter (private val comments: List<Comment>):
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>()
{
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.comment, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}