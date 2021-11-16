package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.CategoryFiltersListBinding
import com.example.complamap.databinding.FragmentListBinding
import com.example.complamap.viewmodel.ListViewModel

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding: FragmentListBinding
    private lateinit var listViewModel: ListViewModel
    private lateinit var recycler: RecyclerView
    private var isFilterShowing = false

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
        recycler = binding.recycler
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        listViewModel.setFilter(null)
        listViewModel.getComplaints { list ->
            recycler.adapter = ComplaintAdapter(list, listViewModel)
        }
        recycler.layoutManager = LinearLayoutManager(this.context)
        binding.filter.setOnClickListener {
            if (!isFilterShowing) {
                showFilters()
            }
        }
    }

    private fun showFilters() {
        isFilterShowing = true
        val rootLayout: ViewGroup? = view?.findViewById(R.id.root_list_layout)
//        val filterRecycler = view?.findViewById<RecyclerView>(R.id.filter_recycler)
//        filterRecycler?.adapter = FilterAdapter(
//            resources.getStringArray(R.array.SpinnerItems),
//            listViewModel
//        )
        val bindingFilter = CategoryFiltersListBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(bindingFilter.root)

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT

        bindingFilter.category1.setOnClickListener {
            updateList(ListViewModel.Filter("category", "Категория 1"))
            isFilterShowing = false
            popupWindow.dismiss()
        }

        bindingFilter.category2.setOnClickListener {
            updateList(ListViewModel.Filter("category", "Категория 2"))
            isFilterShowing = false
            popupWindow.dismiss()
        }

        bindingFilter.category3.setOnClickListener {
            updateList(ListViewModel.Filter("category", "Категория 3"))
            isFilterShowing = false
            popupWindow.dismiss()
        }

        bindingFilter.dismissFilters.setOnClickListener {
            updateList(null)
            isFilterShowing = false
            popupWindow.dismiss()
        }

        rootLayout?.setOnClickListener {
            isFilterShowing = false
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(binding.filter)
    }

    private fun updateList(filter: ListViewModel.Filter?) {
        listViewModel.setFilter(filter)
        listViewModel.getComplaints { list ->
            recycler.adapter = ComplaintAdapter(list, listViewModel)
        }
    }

}
