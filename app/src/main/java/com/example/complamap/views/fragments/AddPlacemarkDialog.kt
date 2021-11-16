package com.example.complamap.views.fragments


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.complamap.R
import com.example.complamap.views.activities.CreateComplaintActivity
import com.yandex.mapkit.geometry.Point

class AddPlacemarkDialog(
    private val address: String,
    private val point: Point
    ): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.add_complaint_question)
                .setMessage("Aдрес: $address")
                .setPositiveButton(R.string.add
                ) { _, _ ->
                    val intent = Intent(requireContext(), CreateComplaintActivity::class.java)
                    intent.apply {
                        putExtra(EXTRA_ADDRESS, address)
                        putExtra(EXTRA_LONGITUDE, point.longitude)
                        putExtra(EXTRA_LATITUDE, point.latitude)
                    }
                    startActivity(intent)
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
        }!!
    }
    companion object{
        const val EXTRA_ADDRESS = "ADDRESS_STRING"
        const val EXTRA_LONGITUDE = "LONGITUDE_DOUBLE"
        const val EXTRA_LATITUDE = "LATITUDE_DOUBLE"
    }
}