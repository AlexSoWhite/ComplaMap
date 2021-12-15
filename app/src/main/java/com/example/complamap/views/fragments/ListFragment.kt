package com.example.complamap.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
    private var searchFL = true

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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = binding.recycler
        val hints: Array<out String> = resources.getStringArray(R.array.address_hints)
        activity?.let {
            ArrayAdapter(it, android.R.layout.simple_list_item_1, hints).also { adapter ->
                binding.input.setAdapter(adapter)
            }
        }
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        updateList(ListViewModel.Filter("default", "default"))
        recycler.layoutManager = LinearLayoutManager(this.context)
        binding.input.setOnKeyListener(
            View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    val text = binding.input.text.toString()
                    if (text == "") {
                        search("default", "default", searchFL)
                        searchFL = !searchFL
                    } else {
                        if (!searchFL) {
                            searchFL = true
                        }
                        search("address", text, searchFL)
                    }
                    view.let { activity?.hideKeyboard(it) }
                    binding.input.clearFocus()
                    return@OnKeyListener true
                }
                false
            }
        )
        binding.searchButton.setOnClickListener {
            if (searchFL) {
                if (binding.input.text.toString() != "") {
                    search("address", binding.input.text.toString(), searchFL)
                }
            } else {
                binding.input.text.clear()
                search("default", "default", searchFL)
            }
        }
        binding.filter.setOnClickListener {
            showFilters()
        }
    }

    private fun showFilters() {
        filterContract.launch("")
    }

    private fun updateList(filter: ListViewModel.Filter?) {
        listViewModel.setFilter(filter)
        val loader: View? = activity?.findViewById(R.id.list_loader)
        loader?.visibility = View.VISIBLE
        listViewModel.getComplaints { list ->
            if (list.isEmpty()) {
                Toast.makeText(
                    context,
                    "ничего не найдено",
                    Toast.LENGTH_SHORT
                ).show()
            }
            loader?.visibility = View.INVISIBLE
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

    private fun search(key: String, value: Any, flag: Boolean) {
        updateList(ListViewModel.Filter(key, value))
        if (flag) {
            binding.searchButton.setImageResource(R.drawable.ic_baseline_search_off_24)
        } else {
            binding.searchButton.setImageResource(R.drawable.ic_baseline_search_24)
        }
        searchFL = !flag
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE)
            as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
