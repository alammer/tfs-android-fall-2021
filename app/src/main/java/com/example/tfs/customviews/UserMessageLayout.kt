package com.example.tfs.customviews

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.spToPixels
import kotlin.math.max

class UserMessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    userName: String = "Anonimous",
    userMessage: String = "Error! Message not found!",
    timeStamp: String? = null
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var name = ""
    private var nameWidth = 0
    private var message = ""
    private var messageWidth = 0
    private val backgroundRect = RectF()
    private val nameBounds = Rect()
    private val messageBounds = Rect()
    private val nameCoordinate = PointF()
    private val messageCoordinate = PointF()
    private var staticLayout: StaticLayout? = null

    private val textColor = Color.parseColor("#FAFAFA")

    private val namePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#2A9D8F")
        textSize = 14.spToPixels()
        textAlign = Paint.Align.LEFT
    }

    private val messagePaint = Paint().apply {
        isAntiAlias = true
        color = textColor
        textSize = 16.spToPixels()
        textAlign = Paint.Align.LEFT
    }

    private val staticPaint = TextPaint().apply {
        isAntiAlias = true
        color = textColor
        textSize = 16.spToPixels()
        textAlign = Paint.Align.LEFT
    }
    private val backPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#1C1C1C")
    }

    init {
        name = userName
        nameWidth = namePaint.measureText(name).toInt()

        message = userMessage
        messageWidth = messagePaint.measureText(message).toInt()

        if (messageWidth >= MAX_WIDTH) {
            multiLineDraw(message)
        } else {
            singleLineDraw(message)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (name.isNotBlank()) {
            namePaint.getTextBounds(name, 0, name.length, nameBounds)

            val nameHeight = nameBounds.height()
            val messageHeight = staticLayout?.height ?: messageBounds.height()

            val totalWidth = max(nameWidth, messageWidth) + END_PADDING + START_PADDING
            val totalHeight =
                nameHeight + messageHeight + TOP_PADDING + INNER_PADDING + BOTTOM_PADDING

            setMeasuredDimension(totalWidth, totalHeight)

        } else {
            setMeasuredDimension(0, 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        backgroundRect.apply {
            top = 0f
            left = 0f
            right = w.toFloat()
            bottom = h.toFloat()
        }

        nameCoordinate.x = 0f + START_PADDING
        nameCoordinate.y = 0f + TOP_PADDING + nameBounds.height()

        if (staticLayout != null) {
            messageCoordinate.x = 0f + START_PADDING
            messageCoordinate.y = 0f + TOP_PADDING + nameBounds.height() + INNER_PADDING
        } else {
            messageCoordinate.x = 0f + START_PADDING //+ messageBounds.width() / 2
            messageCoordinate.y =
                0f + TOP_PADDING + messageBounds.height() + INNER_PADDING + messageBounds.height()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            backgroundRect,
            VIEW_BG_RECT_RADIUS, VIEW_BG_RECT_RADIUS, backPaint
        )
        if (name.isNotBlank()) {
            canvas.drawText(name, nameCoordinate.x, nameCoordinate.y, namePaint)

            if (staticLayout != null) {
                canvas.save()
                canvas.translate(messageCoordinate.x, messageCoordinate.y)
                staticLayout?.draw(canvas)
                canvas.restore()
            } else {
                canvas.drawText(message, messageCoordinate.x, messageCoordinate.y, messagePaint)
            }
        }
    }

    private fun multiLineDraw(message: String) {
        staticLayout = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            StaticLayout(
                message,
                staticPaint,
                MAX_WIDTH,
                Layout.Alignment.ALIGN_NORMAL,
                1f,
                0f,
                false
            )
        } else {
            StaticLayout.Builder
                .obtain(message, 0, message.length, staticPaint, MAX_WIDTH)
                .build()
        }
        staticLayout?.apply {
            messageWidth = width
        }
    }

    private fun singleLineDraw(message: String) {
        messagePaint.getTextBounds(message, 0, message.length, messageBounds)
    }

    companion object {
        private val VIEW_BG_RECT_RADIUS = 18.dpToPixels().toFloat()
        private val START_PADDING = 12.dpToPixels()
        private val END_PADDING = 4.dpToPixels()
        private val TOP_PADDING = 8.dpToPixels()
        private val INNER_PADDING = 4.dpToPixels()
        private val BOTTOM_PADDING = 20.dpToPixels()
        private val MAX_WIDTH = 265.dpToPixels() - END_PADDING - START_PADDING
        private const val DEFAUL_BG_COLOR = "            android:textColorHint=\"@color/edit_hint_color\""
    }

}