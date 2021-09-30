package com.example.tfs

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.button.MaterialButton

class SecondActivity : AppCompatActivity() {

    private lateinit var contactReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnStartService = findViewById<MaterialButton>(R.id.btnStartService)

        btnStartService.setOnClickListener {
            //TODO("Start service")
        }
    }

    override fun onStart() {
        super.onStart()
        contactReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val bundle = intent.extras
                if (bundle != null) {
                    if (bundle.containsKey("contacts")) {
                        val contactList = bundle.getString("contacts")
                        sendResult(contactList)
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(contactReceiver,  IntentFilter("GET_CONTACTS"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactReceiver)
    }

    private fun sendResult(contactList: String?) {
        setResult(Activity.RESULT_OK, Intent().putExtra("TEST_STRING", contactList))
        finish()
    }
}