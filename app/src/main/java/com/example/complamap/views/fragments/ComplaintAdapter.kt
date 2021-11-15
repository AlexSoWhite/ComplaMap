package com.example.complamap.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.model.Complaint
import com.example.complamap.views.activities.ComplaintActivity
import com.orhanobut.hawk.Hawk
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ComplaintAdapter(private val complaints: List<Complaint>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        this.context = parent.context
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        holder.bind(complaints[position], context)
    }

    override fun getItemCount(): Int {
        return complaints.size
    }

    class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var location = itemView.findViewById<TextView>(R.id.address)
        private var status = itemView.findViewById<TextView>(R.id.status)
        private var description = itemView.findViewById<TextView>(R.id.description)
        private var date = itemView.findViewById<TextView>(R.id.date)

        @SuppressLint("SetTextI18n")
        fun bind(complaint: Complaint, context: Context) {
            location.text = "адрес: " + "пока не сконвертировано"
            status.text = "статус: " + complaint.status.toString()
            description.text = "описание: " + complaint.description.toString()
            val df: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            if (complaint.creation_date != null) {
                date.text = df.format(complaint.creation_date.toDate())
            }
            else {
                date.text = null
            }
            val item: ConstraintLayout = itemView.findViewById(R.id.list_item)
            item.setOnClickListener {
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
