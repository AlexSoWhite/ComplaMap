package com.example.complamap.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.SignInViewModel
import com.example.complamap.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        binding.enter.setOnClickListener {
            login()
        }
        binding.goToRegistration.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        signInViewModel.login(email, password)
    }

    companion object {
        private val TAG = "SignIn"
    }
}