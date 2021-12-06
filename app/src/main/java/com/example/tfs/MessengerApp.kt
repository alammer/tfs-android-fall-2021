package com.example.tfs

import android.app.Application
import android.content.Context
import com.example.tfs.di.AppComponent
import com.example.tfs.di.AppDI
import com.example.tfs.di.DaggerAppComponent


class MessengerApp : Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        appComponent = DaggerAppComponent.builder()
            .context(context = this)
            .build()

        appComponent
        /*val preferences = appContext.getSharedPreferences(ZULIP_PREFERENCES_NAME, Context.MODE_PRIVATE)
        if (!preferences.contains(ZULIP_OWNER_ID_KEY)) {
            preferences.edit().putInt(ZULIP_OWNER_ID_KEY, ZULIP_OWNER_ID).apply()
        }
        AppDI.init(preferences)*/
    }

    companion object {
        lateinit var appContext: Context
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
