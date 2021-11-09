package com.example.complamap.views.activities

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.complamap.databinding.ActivityMainBinding
import android.widget.EditText
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.complamap.views.fragments.ListFragment
import com.example.complamap.R
import com.example.complamap.model.ContextContainer
import com.example.complamap.views.fragments.MapFragment
import com.example.complamap.views.fragments.PhotoFragment
import com.example.complamap.views.fragments.ProfileFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.orhanobut.hawk.Hawk
import com.yandex.mapkit.MapKitFactory

const val MAP_IS_INITIALIZE: String = "MAP_IS_INITIALIZE"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        ContextContainer.setContext(application)
        Hawk.init(applicationContext).build()
        savedInstanceState?.getBoolean(MAP_IS_INITIALIZE) ?: let { // Если null, то активность ни разу не создавалась - инициализируем карту
            MapKitFactory.setApiKey(resources.getString(R.string.MapKitApi_Key))
            MapKitFactory.initialize(this)
        }
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mainActivity.getForeground().setAlpha(0)
        val view = binding.root
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(binding.container.id, MapFragment())
                .commit()
        }
        val bottomSheetParent = binding.bottomSheetParent.root
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetParent)
        val tv = TypedValue()
        if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data,
                resources.displayMetrics
            )
            bottomSheetBehavior.peekHeight = actionBarHeight * 2
        }
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    if (bottomSheetParent.isVisible) {
                        bottomSheetParent.visibility = View.GONE
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, ProfileFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.list -> {
                    if (bottomSheetParent.isVisible) {
                        bottomSheetParent.visibility = View.GONE
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, ListFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.map -> {
                    if (bottomSheetParent.isGone) {
                        bottomSheetParent.visibility = View.VISIBLE
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, MapFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.photo -> {
                    if (bottomSheetParent.isVisible) {
                        bottomSheetParent.visibility = View.GONE
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, PhotoFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, CreateComplaintActivity::class.java)
            startActivity(intent)
        }
    }

    // make EditText loose focus on tap outside (applies to Activity and Fragments)
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus ?: let {
                return@dispatchTouchEvent super.dispatchTouchEvent(ev)
            }
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.run {
            putBoolean(MAP_IS_INITIALIZE, true)
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }
}
