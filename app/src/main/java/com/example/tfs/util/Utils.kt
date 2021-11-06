package com.example.tfs.util

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.ContactsContract
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.tfs.R
import com.google.android.material.imageview.ShapeableImageView
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*


fun ShapeableImageView.drawUserInitials(name: String, size: Int) {
    val config = Bitmap.Config.ARGB_8888 // see other conf types
    val bitmap = Bitmap.createBitmap(size, size, config) // this creates a MUTABLE bitmap
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    paint.apply {
        textAlign = Paint.Align.CENTER
        textSize = size / 2.5f
        color = Color.WHITE
    }

    val offset = (paint.descent() + paint.ascent()) / 2
    val userInitials = name.split(' ')
        .mapNotNull { it.firstOrNull()?.toString() }
        .reduce { acc, s -> acc + s }

    canvas.drawText(userInitials, size / 2f, size / 2f - offset, paint)
    setImageBitmap(bitmap)
}

fun View.hideSoftKeyboard() {
    try {
        val im: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    if (this is EditText) {
        text.clear()
    }
    clearFocus()
}

fun TextView.setUserState(userState: Int) {
    text = if (userState == 1) {
        setTextColor(ContextCompat.getColor(context, R.color.state_color_green))
        context.getString(R.string.profile_user_online_state)
    } else {
        setTextColor(ContextCompat.getColor(context, R.color.user_status_text_color))
        context.getString(R.string.profile_user_offline_state)
    }
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


