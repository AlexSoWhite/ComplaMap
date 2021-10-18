package com.example.complamap


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.complamap.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(binding.container.id, MapFragment())
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            binding.fab.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_add_24))
            when (it.itemId) {
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(binding.container.id, ProfileFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.list   -> {
                    supportFragmentManager.beginTransaction().replace(binding.container.id, ListFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.map -> {
                    supportFragmentManager.beginTransaction().replace(binding.container.id, MapFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.photo -> {
                    supportFragmentManager.beginTransaction().replace(binding.container.id, PhotoFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

        binding.fab.setOnClickListener{
            binding.fab.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_check_24))

            supportFragmentManager.beginTransaction().replace(binding.container.id, CreateComplaintFragment())
                .commit()


        }
    }



}