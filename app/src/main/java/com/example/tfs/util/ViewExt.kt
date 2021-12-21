package com.example.tfs.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.material.imageview.ShapeableImageView


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

val Int.spToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()



