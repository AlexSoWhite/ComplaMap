package com.example.complamap.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R

class FilterAdapter(
    private var filters: List<String>,
    private val callback: (String) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(
            filters[position],
            callback
        )
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    class FilterViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView = itemView.findViewById(R.id.filter_text)
        val item: FrameLayout = itemView.findViewById(R.id.filter_item)

        fun bind(filter: String, callback: (String) -> Unit) {
            textView.text = filter
            item.setOnClickListener {
                callback(filter)
            }
        }
    }
}
