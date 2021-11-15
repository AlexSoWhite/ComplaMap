package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.complamap.R
import com.example.complamap.databinding.FragmentListBinding
import com.example.complamap.viewmodel.ListViewModel

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = binding.recycler
        val listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        listViewModel.getComplaints { list ->
            recycler.adapter = ComplaintAdapter(list, listViewModel)
        }
        recycler.layoutManager = LinearLayoutManager(this.context)
    }
}
