package com.example.complamap.model

object UserManager {

    private var user: User? = null
    init {
        user = getUserFromCache()
    }

    fun getCurrentUser(): User? {
        return user
    }

    private fun getUserFromCache(): User? {
        return UserRepository.getUserFromCache()
    }

    fun setUser(user: User?) {
        this.user = user
    }

    fun deleteUserFromCache() {
        UserRepository.deleteUserFromCache()
    }
}
