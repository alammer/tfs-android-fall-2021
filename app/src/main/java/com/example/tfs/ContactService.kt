package com.example.tfs

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ContactService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("Contact", "Function called: onStartCommand()")
        val intent = Intent()
        intent.action = "GET_CONTACTS"
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent.putExtra("contacts", "EMPTY_CONTACT_LIST"))
        return START_STICKY
    }

    // execution of the service will
    // stop on calling this method
    override fun onDestroy() {
        super.onDestroy()

        // stopping the process
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}