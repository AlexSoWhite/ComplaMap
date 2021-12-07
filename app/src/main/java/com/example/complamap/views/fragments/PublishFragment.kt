package com.example.complamap.views.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentPublishBinding
import com.example.complamap.model.ComplaintManager
import com.example.complamap.viewmodel.ComplaintViewModel
import java.util.*
import kotlin.concurrent.schedule

class PublishFragment : Fragment(R.layout.fragment_publish) {
    private lateinit var binding: FragmentPublishBinding

    companion object {
        private var uri: Uri? = null

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
        var confirmed = false

        binding.Confirm.setOnClickListener {
            if (!confirmed) {
                val complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]
                complaintViewModel.putComplaintToDatabase(
                    ComplaintManager.getCurrentComplaint()!!,
                    uri
                ) {
                    confirmed = true
                    Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Жалоба уже опубликована", Toast.LENGTH_SHORT).show()
            }
            Timer().schedule(1000) {
                activity?.setResult(AppCompatActivity.RESULT_OK)
                activity?.finish()
            }
        }
    }
}
