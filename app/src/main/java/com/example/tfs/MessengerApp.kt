package com.example.tfs

import android.app.Application
import android.content.Context

class MessengerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}