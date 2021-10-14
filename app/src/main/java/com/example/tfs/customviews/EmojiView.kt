package com.example.tfs.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.spToPixels
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import kotlin.math.max


class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    emoji: Int? = null,
    count: Int? = null,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var text = ""
    private val backgroundRect = RectF()
    private val textBounds = Rect()
    private val textCoordinate = PointF()
    private val centerPoint = PointF()
    private var isPlusButton = false
    private var alreadyClicked = false

    private var currentCount = 0
    private val currentEmoji = emoji

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textSize = 14.spToPixels()
        textAlign = Paint.Align.CENTER
    }
    private val backPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = DEFAUL_BG_COLOR
    }
    private val linePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = LINE_WIDTH
        color = Color.WHITE
    }

    init {
        if (currentEmoji == null) isPlusButton = true

        count?.let {
            currentEmoji?.let {
                currentCount = count
                setReaction(currentEmoji, currentCount)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (text.isNotBlank()) {
            textPaint.getTextBounds(text, 0, text.length, textBounds)

            val textHeight = textBounds.height()
            val textWidth = textBounds.width()

            val totalWidth = textWidth + HORIZONTAL_PADDING * 2
            val totalHeight = textHeight + VERTICAL_PADDING * 2

            val resultWidth = resolveSize(totalWidth, widthMeasureSpec)
            val resultHeight = resolveSize(totalHeight, heightMeasureSpec)

            setMeasuredDimension(max(resultWidth, DEFAULT_WIDTH), resultHeight)
        } else {
            setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEGHT)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        backgroundRect.apply {
            top = 0f
            left = 0f
            right = w.toFloat()
            bottom = h.toFloat()
        }

        if (text.isNotBlank()) {
            textCoordinate.x = w / 2f
            textCoordinate.y = h / 2f + textBounds.height() / 2 - textPaint.fontMetrics.descent
        } else {
            centerPoint.x = w / 2f
            centerPoint.y = h / 2f
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(backgroundRect, VIEW_BG_RECT_RADIUS, VIEW_BG_RECT_RADIUS, backPaint)
        if (text.isNotBlank()) {
            canvas.drawText(text, textCoordinate.x, textCoordinate.y, textPaint)
        } else {
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
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!alreadyClicked) {
            when (event?.action) {
                ACTION_DOWN -> onClickReaction()
                else -> return false
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onClickReaction(): Boolean {
        if (!isPlusButton) {
            alreadyClicked = true
            backPaint.color = CHECK_BG_COLOR
            addReaction()
        } else {
            //TODO("add emoji by click on plus")
        }
        return true
    }

    private fun addReaction() {
        val newCount = currentCount + 1
        val newReaction = "$currentEmoji $newCount"
        if (newReaction.length > text.length) {
            text = newReaction
            requestLayout()
        } else {
            text = newReaction
            invalidate()
        }
        currentCount++
    }

    private fun setReaction(emoji: Int, count: Int) {
        val c = StringBuilder().appendCodePoint(emoji)
        text =  "$emoji $count"
        requestLayout()
    }

    companion object {
        private val LINE_LENGHT = 7.dpToPixels().toFloat()
        private val LINE_WIDTH = 2.dpToPixels().toFloat()
        private val VIEW_BG_RECT_RADIUS = 10.dpToPixels().toFloat()
        private val HORIZONTAL_PADDING = 8.dpToPixels()
        private val VERTICAL_PADDING = 4.dpToPixels()
        private val DEFAULT_WIDTH = 45.dpToPixels()
        private val DEFAULT_HEGHT = 30.dpToPixels()
        private const val DEFAUL_BG_COLOR = Color.DKGRAY
        private const val CHECK_BG_COLOR = Color.LTGRAY
    }
}
