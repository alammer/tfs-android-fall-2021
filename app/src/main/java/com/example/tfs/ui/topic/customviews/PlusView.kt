package com.example.tfs.ui.topic.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.tfs.R
import com.example.tfs.util.dpToPixels

class PlusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val backgroundRect = RectF()
    private val centerPoint = PointF()
    private val backColor = ContextCompat.getColor(context, R.color.view_bg)

    private val backPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = backColor
    }

    private val linePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = LINE_WIDTH
        color = Color.WHITE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEGHT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        backgroundRect.apply {
            top = 0f
            left = 0f
            right = w.toFloat()
            bottom = h.toFloat()
        }
        centerPoint.x = w / 2f
        centerPoint.y = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(backgroundRect, VIEW_BG_RECT_RADIUS, VIEW_BG_RECT_RADIUS, backPaint)

        canvas.drawLine(
            centerPoint.x - LINE_LENGHT,
            centerPoint.y,
            centerPoint.x + LINE_LENGHT,
            centerPoint.y,
            linePaint
        )
        canvas.drawLine(
            centerPoint.x,
            centerPoint.y - LINE_LENGHT,
            centerPoint.x,
            centerPoint.y + LINE_LENGHT,
            linePaint
        )

    }

    companion object {
        private val LINE_LENGHT = 7.dpToPixels().toFloat()
        private val LINE_WIDTH = 2.dpToPixels().toFloat()
        private val VIEW_BG_RECT_RADIUS = 10.dpToPixels().toFloat()
        private val DEFAULT_WIDTH = 45.dpToPixels()
        private val DEFAULT_HEGHT = 30.dpToPixels()
    }
}