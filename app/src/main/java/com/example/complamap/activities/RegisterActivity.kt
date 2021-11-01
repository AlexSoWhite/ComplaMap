package com.example.complamap.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import com.example.complamap.R
import com.example.complamap.User
import com.example.complamap.databinding.ActivityRegistrationBinding
import com.google.common.graph.Graph
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_registration
        )
        auth = Firebase.auth
        binding.register.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val repeatedPassword = binding.repeatPassword.text.toString()
            if (password != repeatedPassword) {
                Toast.makeText(applicationContext, "пароли не совпадают",
                    Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch {
                    sendData(email, password)
                }
            }
        }
    }

    private suspend fun sendData(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = User(
                        binding.username.text.toString(),
                        email,
                        0,
                        null,
                        null
                    )
                    val users = db.collection("users")
                    users.document(auth.currentUser!!.uid).set(user)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if (task.exception is FirebaseAuthWeakPasswordException) {
                        binding.password.error = "пароль должен содержать хотя бы 6 символов"
                    }
                    else if (task.exception is FirebaseAuthUserCollisionException) {
                        binding.email.error = "этот email уже используется"
                    }
                    else {
                        Toast.makeText(baseContext, "неизвестная ошибка", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}