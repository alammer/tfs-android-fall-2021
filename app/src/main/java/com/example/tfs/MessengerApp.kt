package com.example.tfs

import android.app.Application
import android.content.Context
import com.example.tfs.di.AppDI

class MessengerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        AppDI.init()
    }

    companion object {
        lateinit var appContext: Context
    }
}