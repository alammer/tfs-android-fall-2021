package com.example.tfs

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ContactService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        getContactsInBackground()
        stopSelf()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getContactsInBackground() {
        val bundle = Bundle()
        try {
            Thread {
                val contactList = getContacts()
                contactList?.let {
                    bundle.putString("status", "OK")
                    bundle.putStringArray("contacts", contactList)
                } ?: bundle.putString("status", "FAILED")
                sendBroadcastData(bundle)
            }.start()
        } catch (e: InterruptedException) {
            bundle.putString("status", "ERROR")
            bundle.putString("error", e.message)
            sendBroadcastData(bundle)
        }
    }


    private fun getContacts(): Array<String>? {
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

        return contactList.toTypedArray()
    }

    private fun sendBroadcastData(data: Bundle) {
        val broadcastIntent = Intent()
        broadcastIntent.action = "GET_CONTACTS"
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.putExtras(data))
    }
}