package com.example.tfs.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.tfs.R
import com.example.tfs.util.DpToPixels
import com.example.tfs.util.SPtoPixels

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    emoji: String,
    count: Int,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var text = ""

    private var currentCount = count

    private val textBounds = Rect()
    private val textCoordinate = PointF()

    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 14.SPtoPixels()
        textAlign = Paint.Align.CENTER
    }
    private val backPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
//        val typedArray: TypedArray = context.obtainStyledAttributes(
//            attrs,
//            R.styleable.EmojiView,
//            defStyleAttr,
//            defStyleRes
//        )
//
//        typedArray.recycle()
        setReaction("😄", count)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        Log.i("Eview", "Function called: onMeasure() $text ${text.length} $textBounds")

        val textHeight = textBounds.height()
        val textWidth = textBounds.width()

        val totalWidth = textWidth + HORIZONTAL_PADDING * 2
        val totalHeight = textHeight + VERTICAL_PADDING * 2

        Log.i("Ev", "Function called: onMeasure() $totalWidth $totalHeight")

        val resultWidth = resolveSize(totalWidth, widthMeasureSpec)
        val resultHeight = resolveSize(totalHeight, heightMeasureSpec)

        Log.i("Ev", "Function called: onMeasure() $resultWidth $resultHeight")

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textCoordinate.x = w / 2f
        textCoordinate.y = h / 2f + textBounds.height() / 2 - textPaint.fontMetrics.descent
    }

    override fun onDraw(canvas: Canvas) {
        //canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), VIEW_BG_RECT_RADIUS, VIEW_BG_RECT_RADIUS, backPaint)
        canvas.drawText(text, textCoordinate.x, textCoordinate.y, textPaint)
    }


    private fun setReaction(emoji: String, count: Int) {
        text = "$emoji $count"
        requestLayout()
    }


    companion object {
        //private var INTERVAL = 5.DpToPixels().toFloat()
        private var VIEW_BG_RECT_RADIUS = 10.DpToPixels().toFloat()
        private var HORIZONTAL_PADDING = 8.DpToPixels()
        private var VERTICAL_PADDING = 4.DpToPixels()
    }
}
