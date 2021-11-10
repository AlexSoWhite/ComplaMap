package com.example.complamap.views.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.complamap.R
import com.example.complamap.databinding.ActivitySignInBinding
import com.example.complamap.model.LoginResult
import com.example.complamap.viewmodel.SignInViewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
    }

    override fun onStart() {
        super.onStart()
        val signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        binding.login.setOnClickListener {
            signInViewModel.login(
                binding.email.text.toString(),
                binding.password.text.toString()
            ) { res ->
                when (res) {

                    is LoginResult.Error -> Toast.makeText(
                        application,
                        res.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is LoginResult.Success -> {
                        Toast.makeText(
                            application,
                            "успешный вход",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.goToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
