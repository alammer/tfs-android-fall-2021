package com.example.tfs.util

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.provider.ContactsContract
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun Context?.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Int.dpToPixels() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.spToPixels() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Long.shortDate
    get() = SimpleDateFormat("d MMM", Locale("ru", "RU"))
        .format(this).replace(".", "")

val Long.fullDate
    get() = SimpleDateFormat("MMM d',' yyyy", Locale("ru", "RU"))
        .format(this).replace(".", "")

val Long.year
    get() = SimpleDateFormat("yyyy")
        .format(this).toInt()

fun Long.startOfDay() = this - (this % 86400000L)

fun Cursor.getContactId() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts._ID))

fun Cursor.getContactName() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

fun Cursor.hasPhoneNumber() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).isNotEmpty()

fun Cursor.getPhoneNumber() =
    getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))


