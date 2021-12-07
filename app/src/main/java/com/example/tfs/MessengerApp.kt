package com.example.tfs

import android.app.Application
import android.content.Context
import com.example.tfs.di.core.AppComponent
import com.example.tfs.di.core.DaggerAppComponent


class MessengerApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
        /*val preferences = appContext.getSharedPreferences(ZULIP_PREFERENCES_NAME, Context.MODE_PRIVATE)
        if (!preferences.contains(ZULIP_OWNER_ID_KEY)) {
            preferences.edit().putInt(ZULIP_OWNER_ID_KEY, ZULIP_OWNER_ID).apply()
        }*/
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is MessengerApp -> appComponent
        else -> applicationContext.appComponent
    }

private const val ZULIP_PREFERENCES_NAME = "zulip"
private const val ZULIP_OWNER_ID_KEY = "zulip_owner_key"
private const val ZULIP_OWNER_ID = 456350
