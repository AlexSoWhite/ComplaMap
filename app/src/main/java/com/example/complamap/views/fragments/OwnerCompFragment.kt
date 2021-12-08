package com.example.complamap.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.databinding.FragmentOwnerComplBarBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.views.activities.ComplaintActivity

class OwnerCompFragment : Fragment() {
    private lateinit var binding: FragmentOwnerComplBarBinding
    private lateinit var complaintViewModel: ComplaintViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOwnerComplBarBinding.inflate(inflater)
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentComplaint = ComplaintManager.getCurrentComplaint()
        binding.delete.setOnClickListener {
            if (currentComplaint != null) {
                currentComplaint.compId?.let { it -> complaintViewModel.deleteComplaint(it) }
                Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
        binding.edit.setOnClickListener {
            (activity as ComplaintActivity).makeEditable()
        }
    }
}
