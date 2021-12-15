package com.example.complamap.model

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
                            rating = 0,
                            subs = mutableListOf(),
                            uid = auth.currentUser?.uid
                        )
                        viewModelScope.launch {
                            db.collection("users").document(auth.currentUser!!.uid).set(user)
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
                            val user: User = getUserFromDatabase(auth.currentUser!!.uid)!!
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

    fun putUserToCache(user: User) {
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

    suspend fun getUserFromDatabase(userId: String): User? {
        val userData = db.collection("users").document(userId).get().await()
        return userData.toObject(User::class.java)
    }

    fun updateUser(
        uri: Uri?,
        username: String,
        callback: (String) -> Unit
    ) {
        val user = UserManager.getCurrentUser()
        if (uri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val pictureRef = storageRef.child("profilePics/${user?.uid}")

            val uploadTask = pictureRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                pictureRef.downloadUrl.addOnSuccessListener {
                    deleteUserFromCache()
                    user?.profilePic = it.toString()
                    user?.username = username
                    db.collection("users")
                        .document(user?.uid!!)
                        .update(
                            mapOf(
                                "username" to user.username,
                                "email" to user.email,
                                "profilePic" to user.profilePic,
                                "rating" to user.rating,
                                "subs" to user.subs,
                                "uid" to user.uid
                            )
                        )
                    UserManager.setUser(user)
                    putUserToCache(user)
                    callback("данные обновлены")
                }
            }.addOnFailureListener {
                callback("ошибка")
            }
        } else {
            deleteUserFromCache()
            user?.username = username
            db.collection("users")
                .document(user?.uid!!)
                .update(
                    mapOf(
                        "username" to user.username,
                        "email" to user.email,
                        "profilePic" to user.profilePic,
                        "rating" to user.rating,
                        "subs" to user.subs,
                        "uid" to user.uid
                    )
                )
            UserManager.setUser(user)
            putUserToCache(user)
            callback("данные обновлены")
        }
    }

    fun addSubsToUser(userId: String, sub: String) {
        db.collection("users").document(userId).update("subs", FieldValue.arrayUnion(sub))
    }

    fun removeSubsFromUser(userId: String, sub: String) {
        db.collection("users").document(userId).update("subs", FieldValue.arrayRemove(sub))
    }

    fun editRating(userId: String, rating: Int) {
        db.collection("users").document(userId).update(mapOf("rating" to rating))
    }
}
