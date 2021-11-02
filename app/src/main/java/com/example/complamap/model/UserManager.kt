package com.example.complamap.model

import com.example.complamap.User
import com.orhanobut.hawk.Hawk

object UserManager {

    private var user: User? = null
    init {
        user = getUserFromCache()
    }

    fun getCurrentUser(): User? {
        return user
    }

    private fun getUserFromCache(): User? {
        if (Hawk.isBuilt()) {
            return Hawk.get("user", null)
        }
        return null
    }

    fun setUser(user: User?) {
        this.user = user
        // кладем нового юзера в кэш
    }

    fun deleteUserFromCache() {
        if (Hawk.isBuilt()) {
            Hawk.delete("user")
        }
    }
}
