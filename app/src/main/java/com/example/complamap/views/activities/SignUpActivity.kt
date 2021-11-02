package com.example.complamap.views.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivitySignUpBinding
import com.example.complamap.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_sign_up
        )
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.register.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatedPassword = binding.repeatPassword.text.toString()
        val username = binding.username.text.toString()
        if (password != repeatedPassword) {
            Toast.makeText(
                applicationContext,
                "пароли не совпадают",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            signUpViewModel.register(email, password, username)
        }
    }
}
