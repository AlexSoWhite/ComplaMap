package com.example.complamap.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivitySignInBinding
import com.example.complamap.viewmodel.SignInViewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        binding.login.setOnClickListener {
            login()
        }
    }

    override fun onStart() {
        super.onStart()
        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        signInViewModel.state.observe(this) {

        }
    }

    private fun login() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        // TODO двустороннее общение
        signInViewModel.login(email, password)
    }
}
