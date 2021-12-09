package com.example.tfs.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import com.example.tfs.R
import com.example.tfs.network.utils.NetworkConstants
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso


fun ShapeableImageView.drawUserInitials(name: String) {

    val width = layoutParams.width
    val config = Bitmap.Config.ARGB_8888 // see other conf types
    val bitmap = Bitmap.createBitmap(width, width, config) // this creates a MUTABLE bitmap
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    canvas.drawCircle(width / 2f, width / 2f, width / 2f, paint)

    paint.apply {
        textAlign = Paint.Align.CENTER
        textSize = width / 2.5f
        color = Color.WHITE
    }

    val offset = (paint.descent() + paint.ascent()) / 2
    val userInitials = name.split(' ')
        .mapNotNull { it.firstOrNull()?.toString() }
        .reduce { acc, s -> acc + s }

    canvas.drawText(userInitials, width / 2f, width / 2f - offset, paint)
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

fun String.rawContent(): CharSequence {

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
        val bitmap = Picasso.get().load("${NetworkConstants.BASE_URL}$url").get()

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

fun String.stripHtml(): String {
    val htmlRegex = Regex("(<.*?>)|(&[^ а-я]{1,4}?;)")
    val spaceRegex = Regex(" {2,}")
    return this.replace(htmlRegex, "").replace(spaceRegex, " ")
}

val Int.spToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()



