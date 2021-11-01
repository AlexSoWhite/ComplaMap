package com.example.complamap.activities
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.complamap.ListFragment
import com.example.complamap.R
import com.example.complamap.databinding.ActivityMainBinding
import com.example.complamap.fragments.MapFragment
import com.example.complamap.fragments.PhotoFragment
import com.example.complamap.fragments.ProfileFragment
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(resources.getString(R.string.MapKitApi_Key))
        MapKitFactory.initialize(this)
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
                R.id.photo -> {
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

}
