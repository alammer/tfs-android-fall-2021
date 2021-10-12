package com.example.tfs.util

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.provider.ContactsContract
import android.widget.Toast
import android.util.TypedValue




fun Context?.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Int.dpToPixels() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun  Int.pixelsToDp() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.stToPixels() = (this * Resources.getSystem().displayMetrics.scaledDensity).toFloat()


fun Cursor.getContactId() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts._ID))

fun Cursor.getContactName() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

fun Cursor.hasPhoneNumber() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).isNotEmpty()

fun Cursor.getPhoneNumber() =
    getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))


