package com.example.tfs

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ContactService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        getContactsInBackground()
        stopSelf()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i("ContactServiceStop", "Function called: onDestroy()")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getContactsInBackground() {
        val bundle = Bundle()
        try {
            Thread(Runnable {
                bundle.putString("status", "OK")
                bundle.putString("contacts", "TEST_DATA")
            }).start()
        }catch (e: InterruptedException) {
            bundle.putString("status", "ERROR")
            bundle.putString("contacts", e.message)
        }
        sendBroadcastData(bundle)
    }

    private fun sendBroadcastData (data: Bundle?) {
        Log.i("Contact", "Function called: sendBroad()")
        val broadcastIntent = Intent()
        broadcastIntent.action = "GET_CONTACTS"
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.putExtra("result", data))
    }
}