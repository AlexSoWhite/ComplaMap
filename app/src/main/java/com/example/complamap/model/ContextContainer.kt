package com.example.complamap.model

import android.app.Application

object ContextContainer {

    private var initialized = 0
    private lateinit var application: Application

    fun getContext(): Application {
        if (initialized == 0)
            throw UninitializedPropertyAccessException()
        return application
    }

    fun setContext(application: Application) {
        this.application = application
        initialized = 1
    }
}