package com.example.complamap.views.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.complamap.R
import kotlinx.android.synthetic.main.fragment_fifth_screen.view.*
import kotlinx.android.synthetic.main.fragment_first_screen.view.*

class FifthScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fifth_screen, container, false)

        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.next5.setOnClickListener {
            viewPager?.currentItem = 5
        }

        return view
    }
}