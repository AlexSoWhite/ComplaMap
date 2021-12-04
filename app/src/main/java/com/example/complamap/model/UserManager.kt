package com.example.complamap.model

object UserManager {

    private var user: User? = null
    private var isAuthorized: Boolean? = null

    init {
        user = getUserFromCache()
    }

    fun getCurrentUser(): User? {
        return user
    }

    fun setAuthorized(auth: Boolean) {
        isAuthorized = auth
    }

    fun getAuthorized(): Boolean? {
        return isAuthorized
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
