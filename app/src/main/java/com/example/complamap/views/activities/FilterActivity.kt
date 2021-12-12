package com.example.complamap.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.complamap.R
import com.example.complamap.databinding.ActivityFilterBinding
import com.example.complamap.model.Category
import com.example.complamap.views.fragments.FilterAdapter

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private lateinit var filters: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter)
    }

    override fun onStart() {
        super.onStart()

        filters = binding.filterList
        filters.layoutManager = LinearLayoutManager(this)
        val list = mutableListOf(
            "Сбросить фильтры",
            "Мои жалобы"
        )

        enumValues<Category>().forEach {
            list.add(it.category)
        }

        filters.adapter = FilterAdapter(list) {
            when (it) {
                "Мои жалобы" -> {
                    val data = Intent()
                    data.putExtra("category", "")
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
                "Сбросить фильтры" -> {
                    val data = Intent()
                    data.putExtra("category", "drop")
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
                else -> {
                    val data = Intent()
                    data.putExtra("category", it)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            }
        }
    }
}