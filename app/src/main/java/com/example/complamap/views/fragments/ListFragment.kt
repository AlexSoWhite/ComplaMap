package com.example.complamap.views.fragments

import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
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
        updateList(ListViewModel.Filter("default", "default"))
        recycler.layoutManager = LinearLayoutManager(this.context)
        binding.input.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                Toast.makeText(
                    this.context,
                    "coming soon",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun showFilters() {
        isFilterShowing = true
        val rootLayout: ViewGroup? = view?.findViewById(R.id.root_list_layout)
        val bindingFilter = CategoryFiltersListBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(bindingFilter.root)

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT

        bindingFilter.transport.setOnClickListener {
            updateList(ListViewModel.Filter("category", "Транспорт"))
            popupWindow.dismiss()
        }

        bindingFilter.category2.setOnClickListener {
            updateList(ListViewModel.Filter("category", "Категория 2"))
            popupWindow.dismiss()
        }

        bindingFilter.category3.setOnClickListener {
            updateList(ListViewModel.Filter("category", "Категория 3"))
            popupWindow.dismiss()
        }

        bindingFilter.dismissFilters.setOnClickListener {
            updateList(null)
            popupWindow.dismiss()
        }

        rootLayout?.setOnClickListener {
            isFilterShowing = false
            popupWindow.dismiss()
        }

        binding.filter.setOnClickListener {
            isFilterShowing = false
            popupWindow.dismiss()
            binding.filter.setOnClickListener {
                showFilters()
            }
        }

        popupWindow.showAsDropDown(binding.filter)
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
        isFilterShowing = false
        binding.filter.setOnClickListener {
            showFilters()
        }
    }

}
