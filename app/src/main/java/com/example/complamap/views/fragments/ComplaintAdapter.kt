package com.example.complamap.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.ListItemBinding
import com.example.complamap.model.Complaint
import com.example.complamap.model.ContextContainer
import com.example.complamap.viewmodel.ListViewModel
import com.example.complamap.views.activities.ComplaintActivity
import com.orhanobut.hawk.Hawk
import java.util.Locale

class ComplaintAdapter(
    private val complaints: List<Complaint>,
    private val listViewModel: ListViewModel
) : RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        this.context = parent.context
        return ComplaintViewHolder(view, listViewModel)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        holder.imageView.setImageResource(android.R.color.transparent)
        holder.bind(complaints[position], context)
    }

    override fun getItemCount(): Int {
        return complaints.size
    }

    class ComplaintViewHolder(
        itemView: View,
        private val listViewModel: ListViewModel
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = ListItemBinding.bind(itemView)
        val imageView: ImageView = itemView.findViewById(R.id.image)

        @SuppressLint("SetTextI18n")
        fun bind(complaint: Complaint, context: Context) {
            val locale = Locale("ru", "RU")
            val geocoder = Geocoder(ContextContainer.getContext(), locale)
            val address = complaint.location?.let {
                geocoder.getFromLocation(
                    it.latitude,
                    it.longitude,
                    1
                )
            }
            if (address != null) {
                complaint.address = address[0].getAddressLine(0)
            }
            if (complaint.creation_date != null) {
                complaint.creation_day = android.text.format.DateFormat.format(
                    "dd.MM.yyyy",
                    complaint.creation_date.toDate()
                ).toString()
            }
            binding.complaint = complaint
            listViewModel.loadPhoto(
                imageView.context,
                complaint,
                imageView
            )
            binding.listItem.setOnClickListener {
                val intent = Intent(context, ComplaintActivity::class.java)
                putToCache(complaint)
                intent.putExtra("cachedComplaint", "cachedComplaint")
                context.startActivity(intent)
            }
        }

        private fun putToCache(complaint: Complaint) {
            Hawk.put("cachedComplaint", complaint)
        }
    }
}
