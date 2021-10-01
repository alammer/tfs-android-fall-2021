package com.example.tfs

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.button.MaterialButton

class SecondActivity : AppCompatActivity() {

    private val CONTACT_PERMISSION_CODE = 1

    private lateinit var contactReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnStartService = findViewById<MaterialButton>(R.id.btnStartService)

        btnStartService.setOnClickListener {
            if (checkContactPermission()) {
                startService(Intent(this, ContactService::class.java))
            } else {
                requestContactPermission()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        contactReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.i("ContactRecieve", "Function called: onReceive()")
                sendResult(intent.getBundleExtra("contacts"))
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(contactReceiver, IntentFilter("GET_CONTACTS"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactReceiver)
    }

    private fun checkContactPermission(): Boolean {
        //check if permission was granted/allowed or not, returns true if granted/allowed, false if not
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactPermission() {
        //request the READ_CONTACTS permission
        val permission = arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //handle permission request results || calls when user from Permission request dialog presses Allow or Deny
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted, can pick contact
                startService(Intent(this, ContactService::class.java))
            } else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendResult(serviceData: Bundle?) {
        setResult(Activity.RESULT_OK, Intent().putExtra("contacts", serviceData))
        finish()
    }
}