package com.example.tfs

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.tfs.model.UserContact
import com.example.tfs.util.toast
import com.google.android.material.button.MaterialButton

class SecondActivity : AppCompatActivity() {

    private lateinit var contactReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnStartService = findViewById<MaterialButton>(R.id.btnStartService)

        btnStartService.setOnClickListener {
            startService(Intent(this, ContactService::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        contactReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val contactList = intent.extras?.getParcelableArrayList<UserContact>("contacts")
                contactList?.let { contacts ->
                    sendResult(contacts)
                } ?: finish()
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            contactReceiver, IntentFilter(
                LOCAL_INTENT_ACTION
            )
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactReceiver)
    }

    private fun sendResult(serviceData: ArrayList<UserContact>?) {
        serviceData?.let {
            setResult(Activity.RESULT_OK, Intent().putExtra("contacts", serviceData))
        }
        finish()
    }
}