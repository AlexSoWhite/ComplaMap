package com.example.complamap.model

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
                            null,
                            null,
                            null
                        )
                        addUserToDatabase(user)
                        putUserToCache(user)
                        UserManager.setUser(user)
                        val res = LoginResult.Success
                        callback(res)
                    }

                    task.exception is FirebaseAuthWeakPasswordException -> {
                        val res = LoginResult.Error("слишком простой пароль")
                        callback(res)
                    }

                    task.exception is FirebaseAuthUserCollisionException -> {
                        val res = LoginResult.Error("email уже используется")
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

    private suspend fun getUserFromServer(): DocumentSnapshot {
        val userRef = db.collection("users").document(auth.currentUser!!.uid)
        return userRef.get().await()
    }

    private suspend fun convertReferenceToUser(): User {
        val userData = getUserFromServer()
        val username = userData.data?.get("username").toString()
        val email = userData.data?.get("email").toString()
        val rating = userData.data?.get("rating").toString().toDoubleOrNull()

        // TODO get profilePic and subs properly
        val profilePic = userData.data?.get("profilePic").toString()
        val subs = userData.data?.get("subs").toString()

        return User(
            username = username,
            email = email,
            rating = rating,
            profilePic = profilePic,
            subs = null
        )
    }
}
