package com.example.complamap.model

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.complamap.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppRepository(application: Application) {

    private var application: Application = application
    private var userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    init {
        // инициализация кеша
        Hawk.init(application).build()
    }

    fun register(email: String, password: String, username: String) {
//        if (email.isEmpty()) {
//            Toast.makeText(application, "введите email", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (username.isEmpty()) {
//            Toast.makeText(application, "введите имя пользователя", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (password.isEmpty()) {
//            Toast.makeText(application, "введите пароль", Toast.LENGTH_SHORT).show()
//            return
//        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {

                    task.isSuccessful -> {
//                        Toast.makeText(
//                            application,
//                            "успешная регистрация",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        userMutableLiveData.postValue(auth.currentUser)
                        val user = User(
                            username = username,
                            email = email,
                            0,
                            null,
                            null
                        )
                        addUserToDatabase(user)
                        putUserToCache(user)
                        UserManager.setUser(user)
                    }

                    task.exception is FirebaseAuthWeakPasswordException -> {
                        Toast.makeText(
                            application,
                            "слишком короткий пароль",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    task.exception is FirebaseAuthUserCollisionException -> {
                        Toast.makeText(
                            application,
                            "email уже используется",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    suspend fun login(email: String, password: String) {
        if (email.isEmpty()) {
            Toast.makeText(application, "введите email", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(application, "введите пароль", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(application, "успешный вход", Toast.LENGTH_SHORT).show()
                    var user: User
                    GlobalScope.launch {
                        user = convertReferenceToUser()
                        putUserToCache(user)
                        UserManager.setUser(user)
                    }
                } else {
                    Toast.makeText(
                        application,
                        "неверный логин или пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(user: User) {
        db.collection("users").document(auth.currentUser!!.uid).set(user)
    }

    private fun putUserToCache(user: User) {
        Hawk.put("user", user)
    }

    fun getUserMutableLiveData(): MutableLiveData<FirebaseUser> {
        return userMutableLiveData
    }

    private suspend fun getUserFromServer(): DocumentSnapshot {
        val userRef = db.collection("users").document(auth.currentUser!!.uid)
        val querySnapshot = userRef.get().await()
        return querySnapshot
    }

    private suspend fun convertReferenceToUser(): User {
        val userData = getUserFromServer()
        val username = userData.data?.get("username").toString()
        val email = userData.data?.get("email").toString()
        val rating = userData.data?.get("rating").toString().toInt()

        // TODO get profilePic and subs properly
        val profilePic = userData.data?.get("profilePic").toString()
        val subs = userData.data?.get("subs").toString()

        return User(
            username,
            email,
            rating,
            profilePic,
            null
        )
    }
}
