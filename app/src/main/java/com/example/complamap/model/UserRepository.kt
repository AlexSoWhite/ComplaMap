package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object UserRepository : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun register(
        email: String,
        password: String,
        username: String,
        callback: (result: LoginResult) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        val user = User(
                            username = username,
                            email = email,
                            profilePic = null,
                            rating = 0.0,
                            subs = null,
                            uid = auth.currentUser?.uid
                        )
                        viewModelScope.launch {
                            addUserToDatabase(user)
                            putUserToCache(user)
                            UserManager.setUser(user)
                            val res = LoginResult.Success
                            callback(res)
                        }
                    }

                    task.exception is FirebaseAuthWeakPasswordException -> {
                        val res = LoginResult.Error("слишком простой пароль")
                        callback(res)
                    }

                    task.exception is FirebaseAuthUserCollisionException -> {
                        val res = LoginResult.Error("email уже используется")
                        callback(res)
                    }

                    task.exception is FirebaseAuthInvalidCredentialsException -> {
                        val res = LoginResult.Error("некорректный email")
                        callback(res)
                    }
                }
            }
    }

    suspend fun login(email: String, password: String, callback: (result: LoginResult) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        viewModelScope.launch {
                            val user: User = convertReferenceToUser()
                            putUserToCache(user)
                            UserManager.setUser(user)
                            val res = LoginResult.Success
                            callback(res)
                        }
                    }
                    else -> {
                        val res = LoginResult.Error("ошибка")
                        callback(res)
                    }
                }
            }
    }

    private fun addUserToDatabase(user: User) {
        db.collection("users").document(auth.currentUser!!.uid).set(user)
    }

    private fun putUserToCache(user: User) {
        Hawk.put("user", user)
    }

    fun getUserFromCache(): User? {
        if (Hawk.isBuilt()) {
            return Hawk.get("user", null)
        }
        return null
    }

    fun deleteUserFromCache() {
        if (Hawk.isBuilt()) {
            Hawk.delete("user")
        }
    }

    private suspend fun getUserFromServer(): DocumentSnapshot {
        val userRef = db.collection("users").document(auth.currentUser!!.uid)
        return userRef.get().await()
    }

    private suspend fun convertReferenceToUser(): User {
        val userData = getUserFromServer()
        return userData.toObject(User::class.java)!!
    }
}
