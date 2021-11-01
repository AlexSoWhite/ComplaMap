package com.example.complamap.model

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.complamap.Complaint
import com.example.complamap.User
import com.example.complamap.views.activities.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AppRepository(application: Application) {

    private var application: Application
    private var userMutableLiveData: MutableLiveData<FirebaseUser>
    private val auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    init {
        this.application = application
        auth = Firebase.auth
        userMutableLiveData = MutableLiveData()
    }

    fun register(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userMutableLiveData.postValue(auth.currentUser)
                    addUserToDatabase(
                        User(
                            username,
                            email,
                            0,
                            null,
                            null
                        )
                    )
                    Log.d(TAG, "createUserWithEmail:success")
                } else if (task.exception is FirebaseAuthWeakPasswordException) {
                    Toast.makeText(application, "слишком короткий пароль", Toast.LENGTH_SHORT).show()
                } else if (task.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(application, "email уже используется", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    userMutableLiveData.postValue(auth.currentUser)
                    Toast.makeText(application, "успешный вход", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(application, "неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(user: User) {
        db.collection("users").document(auth.currentUser!!.uid).set(user)
    }

    public suspend fun getUser(): User? {
        if (auth.currentUser == null) {
            return null
        }
        val docRef = db.collection("users").document(auth.currentUser!!.uid)
        val querySnapshot = docRef.get().await()
        return User(
            querySnapshot.getString("username"),
            querySnapshot.getString("email"),
            querySnapshot.get("rating") as Int?,
            querySnapshot.get("profilePic") as Uri?,
            querySnapshot.get("subs") as Array<String>?
        )
    }

    fun getUserMutableLiveData(): MutableLiveData<FirebaseUser> {
        return userMutableLiveData
    }
}