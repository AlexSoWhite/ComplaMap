package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.complamap.model.Complaint
import com.example.complamap.ComplaintAdapter
import com.example.complamap.R
import com.example.complamap.databinding.FragmentListBinding

class ListFragment : Fragment(R.layout.fragment_list) {
    private lateinit var binding: FragmentListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        val recycler = binding.recycler
        recycler.adapter = ComplaintAdapter(getComplaints())
        recycler.layoutManager = LinearLayoutManager(this.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getComplaints(): List<Complaint> {
        return listOf(
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint(),
            Complaint()
        )
    }
}
