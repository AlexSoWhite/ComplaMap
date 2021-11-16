package com.example.complamap.views.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.FilterItemBinding
import com.example.complamap.viewmodel.ListViewModel

class FilterAdapter(
    private val filters: Array<String>,
    private val listViewModel: ListViewModel
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        this.context = parent.context
        return FilterViewHolder(view, listViewModel)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    class FilterViewHolder(
        itemView: View,
        private val listViewModel: ListViewModel
    ) : RecyclerView.ViewHolder(itemView)  {

        private val binding = FilterItemBinding.bind(itemView)

        fun bind(filter: String) {
            binding.filterItem.text = filter
            binding.filterItem.setOnClickListener {
                listViewModel.setFilter(ListViewModel.Filter("category", filter))
            }
        }
    }
}