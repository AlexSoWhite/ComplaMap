package com.example.complamap.views.fragments

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.FragmentProfileBinding
import com.example.complamap.model.UserManager
import com.example.complamap.viewmodel.ProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private var mainLayout: FrameLayout? = null
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        mainLayout = activity?.findViewById(R.id.main_activity)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.openPopupBt.setOnClickListener {
            popUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        // here we decide which page to show - authorized or not
        profileViewModel.getUser { user ->
            when (user) {

                null -> {
                    childFragmentManager.commit {
                        replace(R.id.profile_container, NoAuthFragment())
                    }
                    UserManager.setAuthorized(false)
                }

                else -> {
                    childFragmentManager.commit {
                        replace(R.id.profile_container, AuthorizedUserFragment())
                    }
                    UserManager.setAuthorized(true)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        profileViewModel.getUser { user ->
            when (user) {

                null -> {
                    if (UserManager.getAuthorized() == true) {
                        childFragmentManager.commit {
                            replace(R.id.profile_container, NoAuthFragment())
                        }
                        UserManager.setAuthorized(false)
                    }
                }

                else -> {
                    if (UserManager.getAuthorized() == false) {
                        childFragmentManager.commit {
                            replace(R.id.profile_container, AuthorizedUserFragment())
                        }
                        UserManager.setAuthorized(true)
                    }
                }
            }
        }
    }

    private fun popUp() {
        val inflater: LayoutInflater =
            (requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val view = inflater.inflate(R.layout.activity_pop_up_guide, null)
        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 10.0F
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn
//            TODO exitTransition?
        }
        popupWindow.setOnDismissListener {
            mainLayout?.foreground?.alpha = 0
        }
        TransitionManager.beginDelayedTransition(binding.rootLayout)
        popupWindow.showAtLocation(
            binding.rootLayout,
            Gravity.CENTER,
            0,
            0
        )
        mainLayout?.foreground?.alpha = 50
    }
}
