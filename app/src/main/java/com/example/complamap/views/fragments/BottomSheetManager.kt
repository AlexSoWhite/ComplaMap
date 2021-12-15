package com.example.complamap.views.fragments

import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.complamap.R
import com.example.complamap.databinding.FragmentMapBinding
import java.security.InvalidParameterException

object BottomSheetManager {
    var visibleBottomSheet = R.id.bottom_sheet_parent
        set(value) {
            makeGone(field)
            makeVisible(value)
//            if (value != field) {
//                makeGone(field)
//                makeVisible(value)
//            }
            field = value
        }
    lateinit var binding: FragmentMapBinding
    fun toStandardSheet() {
        visibleBottomSheet = R.id.bottom_sheet_parent
    }
    private fun makeVisible(id: Int) {
        when (id) {
            R.id.map_object_info -> {
                changeFABAnchorTo(id)
                binding.mapObjectInfo.visibility = View.VISIBLE
            }
            R.id.complaint_info -> {
                changeFABAnchorTo(id)
                binding.complaintInfo.visibility = View.VISIBLE
            }
            R.id.bottom_sheet_parent -> {
                changeFABAnchorTo(id)
                binding.bottomSheetParent.root.visibility = View.VISIBLE
            }
            else -> throw InvalidParameterException()
        }
    }
    private fun makeGone(id: Int) {
        when (id) {
            R.id.map_object_info -> binding.mapObjectInfo.visibility = View.GONE
            R.id.complaint_info -> binding.complaintInfo.visibility = View.GONE
            R.id.bottom_sheet_parent -> binding.bottomSheetParent.root.visibility = View.GONE
        }
    }
    private fun changeFABAnchorTo(id: Int) {
        val params = binding.fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = id
        params.anchorGravity = Gravity.BOTTOM or Gravity.END
        binding.fab.layoutParams = params
    }
}
