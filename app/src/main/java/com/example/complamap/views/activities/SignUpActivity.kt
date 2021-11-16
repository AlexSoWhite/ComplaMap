package com.example.complamap.views.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivitySignUpBinding
import com.example.complamap.model.LoginResult
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
    }

    override fun onStart() {
        super.onStart()
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.register.setOnClickListener {
            signUpViewModel.register(
                binding.email.text.toString(),
                binding.username.text.toString(),
                binding.password.text.toString(),
                binding.repeatPassword.text.toString()
            ) { res ->
                when (res) {

                    is LoginResult.Error -> Toast.makeText(
                        applicationContext,
                        res.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is LoginResult.Success -> {
                        Toast.makeText(
                            applicationContext,
                            "регистрация прошла успешно",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }
}
