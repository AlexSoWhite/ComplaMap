package com.example.complamap.views.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
                        finish()
                    }
                }
            }
        }

        binding.goToSignUp.setOnClickListener {
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//                when(result.resultCode) {
//                    1 -> finish()
//                }
//            }.launch(Intent(this, SignUpActivity::class.java))
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}

