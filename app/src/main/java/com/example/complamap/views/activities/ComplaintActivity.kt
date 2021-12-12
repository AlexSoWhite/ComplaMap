package com.example.complamap.views.activities

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.complamap.R
import com.example.complamap.databinding.ActivityComplaintBinding
import com.example.complamap.model.Category
import com.example.complamap.model.Complaint
import com.example.complamap.model.ComplaintManager
import com.example.complamap.model.TakePhotoContract
import com.example.complamap.model.UserManager
import com.example.complamap.model.UserRepository
import com.example.complamap.viewmodel.CommentViewModel
import com.example.complamap.viewmodel.ComplaintViewModel
import com.example.complamap.viewmodel.ProfileViewModel
import com.example.complamap.views.fragments.CommentsFragment
import com.example.complamap.views.fragments.OwnerCompFragment
import com.example.complamap.views.fragments.PublishFragment
import com.example.complamap.views.fragments.SaveFragment
import com.example.complamap.views.fragments.ViewerCompFragment
import kotlinx.coroutines.launch

class ComplaintActivity : AppCompatActivity() {
    companion object {
        val categories = mutableListOf<String>()
    }

    private lateinit var complaintViewModel: ComplaintViewModel
    private lateinit var commentViewModel: CommentViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var binding: ActivityComplaintBinding
    private var currentUser: String? = ""
    private var creator: String = ""
    private var currentComplaint: Complaint? = null
    var imageUri: Uri? = null
    private var isEditableMode: Boolean = false
    private val takePhotoLauncher =
        registerForActivityResult(TakePhotoContract()) {
            imageUri = it
            if (it.toString() != "null") {
                binding.photo.setImageURI(it)
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (it in Category.values()) {
            categories.add(it.category)
        }

        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_complaint
        )
        binding.complaintActivity.foreground.alpha = 0
        complaintViewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]

        commentViewModel = ViewModelProvider(this)[CommentViewModel::class.java]

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.getUser { user ->
            currentUser = user?.uid
        }
        creator = ComplaintManager.getCurrentComplaint()?.creator.toString()
        currentComplaint = ComplaintManager.getCurrentComplaint()
        when (intent.getStringExtra("FragmentMode")) {

            "Publish" -> {
                var uri: Uri? = null
                if (!intent.hasExtra("noPhoto")) {
                    uri = intent.getStringExtra("uri")!!.toUri()
                }
                complaintViewModel.loadPhotoFromUri(
                    baseContext,
                    uri,
                    binding.photo
                )
                supportFragmentManager.beginTransaction()
                    .replace(binding.container.id, PublishFragment.getInstance(uri))
                    .commit()
            }

            "View" -> {
                if (currentUser != null) {
                    if (currentUser == creator) {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.container.id, OwnerCompFragment())
                            .commit()
                    } else {
                        val args = Bundle()
                        args.putString("creator", creator)
                        val fr = ViewerCompFragment()
                        fr.arguments = args
                        supportFragmentManager.beginTransaction()
                            .replace(binding.container.id, fr)
                            .commit()
                    }
                }

                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.containerForComments.id,
                        CommentsFragment.getInstance(
                            UserManager.getCurrentUser(),
                            currentComplaint!!
                        )
                    ).commit()

                complaintViewModel.loadPhotoFromServer(
                    baseContext,
                    currentComplaint!!,
                    binding.photo
                )
            }
        }

        binding.complaint = currentComplaint
        lifecycleScope.launch {
            binding.creator = UserRepository.getUserFromDatabase(creator)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.category.adapter = adapter
        val pos = categories.indexOf(currentComplaint?.category)
        pos.let { binding.category.setSelection(it) }
        binding.ExitButton.setOnClickListener {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun makeEditable() {
        isEditableMode = true
        editOptions(isEditableMode)
        binding.categoryTextView.visibility = View.INVISIBLE
        binding.category.visibility = View.VISIBLE
        binding.photo.setOnClickListener {
            if (isEditableMode) {
                takePhotoLauncher.launch("")
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, SaveFragment())
            .commit()
    }

    fun edit() {
        ComplaintManager.getCurrentComplaint()?.compId?.let {
            complaintViewModel.editComplaint(
                it,
                binding.description.text.toString(),
                binding.category.selectedItem.toString(),
                binding.address.text.toString(),
                imageUri
            )
        }
        Toast.makeText(this, "Изменено", Toast.LENGTH_SHORT).show()
        isEditableMode = false
        binding.categoryTextView.text = binding.category.selectedItem.toString()
        binding.categoryTextView.visibility = View.VISIBLE
        binding.category.visibility = View.INVISIBLE
        editOptions(isEditableMode)
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, OwnerCompFragment())
            .commit()
    }

    private fun editOptions(state: Boolean) {
        binding.description.isFocusable = state
        binding.description.isFocusableInTouchMode = state
        binding.description.isCursorVisible = state
        if (currentComplaint?.status != "В работе") {
            binding.address.isFocusable = state
            binding.address.isFocusableInTouchMode = state
            binding.address.isCursorVisible = state
        }
    }
}
