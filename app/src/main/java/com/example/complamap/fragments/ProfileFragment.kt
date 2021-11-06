package com.example.complamap.fragments
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
import com.example.complamap.R
import com.example.complamap.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private var mainLayout: FrameLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        mainLayout = getActivity()?.findViewById(R.id.main_activity)
        val authFragment = NoAuthFragment()
        childFragmentManager.beginTransaction().apply {
            add(R.id.profile_container, authFragment)
            commit()
        }
        binding.openPopupBt.setOnClickListener {
            popUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        popupWindow.setOutsideTouchable(true)
        popupWindow.elevation = 10.0F
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn
//            TODO exitTransition?
        }
        popupWindow.setOnDismissListener {
            mainLayout?.getForeground()?.setAlpha(0)
        }
        TransitionManager.beginDelayedTransition(binding.rootLayout)
        popupWindow.showAtLocation(
            binding.rootLayout,
            Gravity.CENTER,
            0,
            0
        )
        mainLayout?.getForeground()?.setAlpha(50)
    }
}
