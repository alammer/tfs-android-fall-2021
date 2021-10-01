package com.example.tfs

import android.annotation.SuppressLint
import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.provider.ContactsContract
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
                val contactList = getContacts()
                bundle.putString("status", "OK")
                bundle.putString("contacts", "TEST_DATA")
            }).start()
        } catch (e: InterruptedException) {
            bundle.putString("status", "ERROR")
            bundle.putString("contacts", e.message)
        }
        sendBroadcastData(bundle)
    }


    private fun getContacts() {
        val contentResolver: ContentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )
        cursor ?: return

        while (cursor.moveToNext()) {
            val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            Log.i("get Name", "Function called: getContacts() fetch NAME = $name")

            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                Log.i("get Name", "Contact has phone!!!")

                //SQLite throw error on spaces in selection param?
                val cursorPhone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null
                )

                if (cursorPhone?.moveToNext() == true) {
                    val contactNumber = cursorPhone.getString(cursorPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.i("get Number", "It's number: $contactNumber")
                }
            }
        }
    }

    private fun sendBroadcastData(data: Bundle?) {
        Log.i("Contact", "Function called: sendBroad()")
        val broadcastIntent = Intent()
        broadcastIntent.action = "GET_CONTACTS"
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.putExtra("result", data))
    }
}