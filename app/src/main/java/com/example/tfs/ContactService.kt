package com.example.tfs

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.tfs.model.UserContact
import com.example.tfs.util.getContactId
import com.example.tfs.util.getContactName
import com.example.tfs.util.getPhoneNumber
import com.example.tfs.util.hasPhoneNumber

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
                    bundle.putParcelableArrayList("contacts", contactList)
                }
                sendBroadcastData(bundle)
            }.start()
        } catch (e: InterruptedException) {
            sendBroadcastData(bundle)
        }
    }


    private fun getContacts(): ArrayList<UserContact>? {
        val contentResolver: ContentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        ) ?: return null

        val contactList = arrayListOf<UserContact>()

        while (cursor.moveToNext()) {
            val contactId = cursor.getContactId()
            val name = cursor.getContactName()

            var phoneNumber: String? = null

            if (cursor.hasPhoneNumber()
            ) {
                //SQLite throw error on spaces in selection param?
                val cursorPhone = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null
                )

                if (cursorPhone?.moveToNext() == true) phoneNumber = cursorPhone.getPhoneNumber()

                cursorPhone?.close()
            }
            contactList.add(UserContact(name, phoneNumber))
        }
        cursor.close()

        return contactList
    }

    private fun sendBroadcastData(data: Bundle) {
        val broadcastIntent = Intent()
        broadcastIntent.action = LOCAL_INTENT_ACTION
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.putExtras(data))
    }
}

const val LOCAL_INTENT_ACTION = "GET_CONTACTS"