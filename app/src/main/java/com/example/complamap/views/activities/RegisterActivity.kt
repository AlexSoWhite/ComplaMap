package com.example.complamap.views.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.viewmodel.RegisterViewModel
import com.example.complamap.databinding.ActivityRegistrationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_registration
        )
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.register.setOnClickListener {
            register()
        }
    }

    private fun register(){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatedPassword = binding.repeatPassword.text.toString()
        val username = binding.username.text.toString()
        if (password != repeatedPassword) {
            Toast.makeText(applicationContext, "пароли не совпадают",
                Toast.LENGTH_SHORT).show()
        } else {
            GlobalScope.launch {
                registerViewModel.register(email, password, username)
            }
        }
    }
}