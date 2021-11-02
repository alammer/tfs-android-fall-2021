package com.example.tfs.ui.topic.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.tfs.R
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.spToPixels
import kotlin.math.max

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    emojiCode: Int? = null,
    count: Int? = null,
    isClicked: Boolean = false
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var text = ""
    private val backgroundRect = RectF()
    private val textBounds = Rect()
    private val textCoordinate = PointF()

    private var currentCount = 0
    private var emojiGliph = ""

    private val backColor = ContextCompat.getColor(context, R.color.view_bg)
    private val checkBackColor = ContextCompat.getColor(context, R.color.check_emoji_view_bg_color)
    private val textColor = ContextCompat.getColor(context, R.color.emoji_text_color)
    private val checkTextColor = ContextCompat.getColor(context, R.color.check_emoji_text_color)

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = if (!isClicked) {
            textColor
        } else {
            checkTextColor
        }
        textSize = 14.spToPixels()
        textAlign = Paint.Align.CENTER
    }
    private val backPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = if (!isClicked) {
            backColor
        } else {
            checkBackColor
        }
    }

    init {
        count?.let {
            emojiCode?.let {
                currentCount = count
                emojiGliph = StringBuilder().appendCodePoint(emojiCode).toString()
                setReaction(emojiGliph, currentCount)
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
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(backgroundRect, VIEW_BG_RECT_RADIUS, VIEW_BG_RECT_RADIUS, backPaint)
        if (text.isNotBlank()) {
            canvas.drawText(text, textCoordinate.x, textCoordinate.y, textPaint)
        }
    }

    private fun setReaction(emoji: String, count: Int) {
        text = "$emoji $count"
        requestLayout()
    }

    companion object {
        private val VIEW_BG_RECT_RADIUS = 10.dpToPixels().toFloat()
        private val HORIZONTAL_PADDING = 8.dpToPixels()
        private val VERTICAL_PADDING = 4.dpToPixels()
        private val DEFAULT_WIDTH = 45.dpToPixels()
        private val DEFAULT_HEGHT = 30.dpToPixels()
    }
}
