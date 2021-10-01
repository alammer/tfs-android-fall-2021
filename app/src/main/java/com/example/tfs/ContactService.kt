package com.example.tfs

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
            Thread ( Runnable{
                val contactList = getContacts()
                contactList?.let {
                    bundle.putString("status", "OK")
                    bundle.putString("contacts", "WTF???")
                } ?: bundle.putString("status", "ERROR")
                sendBroadcastData(bundle)
                }).start()
        } catch (e: InterruptedException) {
            bundle.putString("status", "ERROR")
            bundle.putString("error", e.message)
            sendBroadcastData(bundle)
        }

    }


    private fun getContacts(): ArrayList<String>? {
        val contentResolver: ContentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        ) ?: return null

        val contactList = arrayListOf<String>()

        while (cursor.moveToNext()) {
            val contactId =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

            var phoneNumber: String? = null

            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    .toInt() > 0
            ) {
                //SQLite throw error on spaces in selection param?
                val cursorPhone = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null
                )

                if (cursorPhone?.moveToNext() == true) {
                    phoneNumber =
                        cursorPhone.getString(cursorPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                }
                cursorPhone?.close()
            }
            contactList.add("$name: $phoneNumber")
        }
        cursor.close()

        return contactList
    }

    private fun sendBroadcastData(data: Bundle?) {
        val broadcastIntent = Intent()
        broadcastIntent.action = "GET_CONTACTS"
        val innerdata = data
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.putExtra("contacts", "wtf????"))
    }
}