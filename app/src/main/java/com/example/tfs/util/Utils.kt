package com.example.tfs.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.ContactsContract
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.tfs.network.utils.NetworkConstants
import com.squareup.picasso.Picasso


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

fun String.tryToParseContentImage(res: Resources): CharSequence {

    val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        (Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT, ImageGetter(res), null))
    } else {
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT, ImageGetter(res), null)
    } as SpannableStringBuilder
    return trimSpannable(spanned).toSpannable()
}

fun String.rawContent(res: Resources): CharSequence {

    val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        (Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT))
    } else {
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    } as SpannableStringBuilder
    return trimSpannable(spanned).toSpannable()
}

class ImageGetter(
    private val res: Resources,
) : Html.ImageGetter {

    override fun getDrawable(url: String): Drawable {

        val holder = BitmapDrawablePlaceHolder(res, null)

        //TODO("сделать регулярку на начало img_ulr, если допускаются ссылки на внешние хостинги")
        val bitmap = Picasso.get().load("${NetworkConstants.SERVER_BASE_URL}$url").get()

        val drawable = BitmapDrawable(res, bitmap)

        //TODO("разобраться с отрисовкой bitmap внутри границ")
        val width = getScreenWidth() - 200.toPx

        val aspectRatio: Float =
            (drawable.intrinsicWidth.toFloat()) / (drawable.intrinsicHeight.toFloat())

        val height = width / aspectRatio

        drawable.setBounds(10, 20, width, height.toInt())
        holder.setDrawable(drawable)
        holder.setBounds(10, 20, width, height.toInt())
        return holder
    }

    internal class BitmapDrawablePlaceHolder(res: Resources, bitmap: Bitmap?) :
        BitmapDrawable(res, bitmap) {
        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.run { draw(canvas) }
        }

        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
        }
    }
    // Function to get screenWidth used above
    private fun getScreenWidth() =
        Resources.getSystem().displayMetrics.widthPixels
}

private fun trimSpannable(spannable: SpannableStringBuilder): SpannableStringBuilder {
    var trimStart = 0
    var trimEnd = 0
    var text = spannable.toString()
    while (text.isNotEmpty() && text.startsWith("\n")) {
        text = text.substring(1)
        trimStart += 1
    }
    while (text.isNotEmpty() && text.endsWith("\n")) {
        text = text.substring(0, text.length - 1)
        trimEnd += 1
    }
    return spannable.delete(0, trimStart).delete(spannable.length - trimEnd, spannable.length)
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

fun Activity.showSystemMessage(text: String, longDuration: Boolean = false) =
    Toast.makeText(this, text, if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
        .show()

fun Fragment.showSystemMessage(text: String, longDuration: Boolean = false) {
    activity?.showSystemMessage(text, longDuration)
}

fun Context?.toast(message: String?) {
    message?.let {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun View.showSnackbar(
    @StringRes stringRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    Snackbar.make(this, stringRes, duration).show()
}

val Int.spToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Long.shortDate
    get() = SimpleDateFormat("d MMM", Locale("ru", "RU"))
        .format(this * 1000L).replace(".", "")

val Long.fullDate
    get() = SimpleDateFormat("d MMMM',' ' 'yyyy", Locale("ru", "RU"))
        .format(this * 1000L).replace(".", "")

val Long.year
    get() = SimpleDateFormat("yyyy")
        .format(this * 1000L).toInt()

fun Long.startOfDay() = this - (this % 86400L)

fun Cursor.getContactId() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts._ID))

fun Cursor.getContactName() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

fun Cursor.hasPhoneNumber() =
    getString(getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).isNotEmpty()

fun Cursor.getPhoneNumber() =
    getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))


