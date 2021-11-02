package com.example.tfs.util

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.provider.ContactsContract
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.material.imageview.ShapeableImageView


fun ShapeableImageView.drawUserInitals(name: String, size: Int) {

    val config = Bitmap.Config.ARGB_8888 // see other conf types
    val bitmap = Bitmap.createBitmap(size, size, config) // this creates a MUTABLE bitmap
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    paint.apply {
        textAlign = Paint.Align.CENTER
        textSize = size / 2f
        color = Color.WHITE
    }

    val offset = paint.descent() + paint.ascent() / 2
    val userInitials = name.split(' ')
        .mapNotNull { it.firstOrNull()?.toString() }
        .reduce { acc, s -> acc + s }
    canvas.drawText(userInitials, size / 2f, size / 2f - offset, paint)
    setImageBitmap(bitmap)
}

fun Context?.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Int.dpToPixels() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.spToPixels() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Long.shortDate
    get() = SimpleDateFormat("d MMM", Locale("ru", "RU"))
        .format(this).replace(".", "")

val Long.fullDate
    get() = SimpleDateFormat("d MMMM',' ' 'yyyy", Locale("ru", "RU"))
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


