package com.example.complamap.views.activities

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.complamap.R
import com.example.complamap.databinding.ActivityMainBinding
import com.example.complamap.model.ContextContainer
import com.example.complamap.views.fragments.ListFragment
import com.example.complamap.views.fragments.MapFragment
import com.example.complamap.views.fragments.ProfileFragment
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
        savedInstanceState?.getBoolean(MAP_IS_INITIALIZE)
            ?: let { // Если null, то активность ни разу не создавалась - инициализируем карту
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
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, ProfileFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.list -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, ListFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.map -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.container.id, MapFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
            }
            false
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
