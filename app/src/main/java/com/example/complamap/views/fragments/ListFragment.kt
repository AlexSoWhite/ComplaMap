package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.FragmentListBinding
import com.example.complamap.model.FilterContract
import com.example.complamap.model.UserManager
import com.example.complamap.viewmodel.ListViewModel

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding: FragmentListBinding
    private lateinit var listViewModel: ListViewModel
    private lateinit var recycler: RecyclerView
    private var fromFilter: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    private val filterContract = registerForActivityResult(FilterContract()) {
        fromFilter = true
        if (it != null) {
            if (it.toString() == "drop") {
                updateList(null)
            } else if (it.toString() == "mine") {
                updateList(ListViewModel.Filter("creator", UserManager.getCurrentUser()?.uid))
            } else if (it.toString() == "followers") {
                updateList(ListViewModel.Filter("followers", UserManager.getCurrentUser()?.uid))
            } else {
                updateList(ListViewModel.Filter("category", it.toString()))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = binding.recycler
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        updateList(ListViewModel.Filter("default", "default"))
        recycler.layoutManager = LinearLayoutManager(this.context)
        binding.input.setOnKeyListener(
            View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    Toast.makeText(
                        this.context,
                        "coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnKeyListener true
                }
                false
            }
        )
        binding.filter.setOnClickListener {
            showFilters()
        }
    }

    private fun showFilters() {
        filterContract.launch("")
    }

    private fun updateList(filter: ListViewModel.Filter?) {
        listViewModel.setFilter(filter)
        listViewModel.getComplaints { list ->
            if (list.isEmpty()) {
                Toast.makeText(
                    context,
                    "ничего не найдено",
                    Toast.LENGTH_SHORT
                ).show()
            }
            recycler.adapter = ComplaintAdapter(list, listViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!fromFilter) {
            updateList(listViewModel.getFilter())
        } else {
            fromFilter = false
        }
    }
}
