package com.example.complamap.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.complamap.R
import com.example.complamap.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        auth = Firebase.auth
        binding.enter.setOnClickListener {
            GlobalScope.launch {
                sendData()
            }
        }
        binding.goToRegistration.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun get(key: String): DocumentSnapshot {
        val docRef = db.collection("users").document(key)
        return docRef.get().await()
    }

    private suspend fun sendData() {
        val password = binding.password.text.toString()
        val email = binding.email.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    // TODO normal auth
                    val user = db.collection("users").document(email)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "аутентификация не удалась",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private val TAG = "SignIn"
    }
}