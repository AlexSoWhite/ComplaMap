package com.example.complamap.views.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentPublishBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.viewmodel.ComplaintViewModel
import java.util.Timer
import kotlin.concurrent.schedule

class PublishFragment : Fragment(R.layout.fragment_publish) {
    private lateinit var binding: FragmentPublishBinding

    companion object {
        private var uri: Uri? = null
        private const val DELAY: Long = 1000

        fun getInstance(uri: Uri?): PublishFragment {
            this.uri = uri
            return PublishFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPublishBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loader: View? = activity?.findViewById(R.id.complaint_create_loader)
        loader?.visibility = View.INVISIBLE
        binding.Confirm.setOnClickListener {
            binding.Confirm.isEnabled = false
            val complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
            loader?.visibility = View.VISIBLE
            complaintViewModel.putComplaintToDatabase(
                ComplaintManager.getCurrentComplaint()!!,
                uri
            ) {
                Toast.makeText(activity, "Опубликовано", Toast.LENGTH_SHORT).show()
                loader?.visibility = View.INVISIBLE
                ComplaintManager.justPublished = true
                Timer().schedule(DELAY) {
                    activity?.setResult(it)
                    activity?.finish()
                }
            }
        }
    }
}
