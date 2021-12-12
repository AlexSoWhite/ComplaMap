package com.example.complamap.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
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
    private var searchFL = true
    private val currFilterKey: MutableMap<String, String> = mutableMapOf("key" to "default")
    private val currFilterValue: MutableMap<String, Any> = mutableMapOf("value" to "default")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
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
            currFilterKey["key"] = "default"
            currFilterValue["value"] = "default"
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

    private fun search(key: String, value: Any, flag: Boolean) {
        updateList(ListViewModel.Filter(key, value))
        currFilterKey["key"] = key
        currFilterValue["value"] = value
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

    override fun onResume() {
        super.onResume()
        updateList(
            currFilterValue["value"]
                ?.let {
                    currFilterKey["key"]?.let { it1 ->
                        ListViewModel.Filter(
                            it1,
                            it
                        )
                    }
                }
        )
    }
}
