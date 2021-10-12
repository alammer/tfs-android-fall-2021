package com.example.tfs.customviews

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.spToPixels
import kotlin.math.max

class UserMessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    userName: String = "Anonimous",
    message: String = "EmptyMessage",
    timeStamp: String? = null
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var name = ""
    private val backgroundRect = RectF()
    private val nameBounds = Rect()
    private val messageBounds = Rect()
    private val textCoordinate = PointF()
    private val centerPoint = PointF()
    private var isPlusButton = false
    private var alreadyClicked = false

    private val userName = userName
    private val message = message

    private val namePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        textSize = 14.spToPixels()
        textAlign = Paint.Align.CENTER
    }

    private val messagePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textSize = 14.spToPixels()
        textAlign = Paint.Align.CENTER
    }

    private val staticPaint = TextPaint().apply {
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


    init {
//        val typedArray: TypedArray = context.obtainStyledAttributes(
//            attrs,
//            R.styleable.EmojiView,
//            defStyleAttr,
//            defStyleRes
//        )
//
//        typedArray.recycle()
        setMessage(userName, message)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (name.isNotBlank()) {
            namePaint.getTextBounds(name, 0, name.length, nameBounds)

            val nameHeight = nameBounds.height()
            val nameWidth = nameBounds.width()

            messagePaint.getTextBounds(message, 0, name.length, messageBounds)

            if (messageBounds.width() < MAX_WIDTH) {
                val messageHeight = messageBounds.height()
                val messageWidth = messageBounds.width()

                val totalWidth = max(nameWidth, messageWidth) + RIGHT_PADDING + LEFT_PADDING
                val totalHeight =
                    nameHeight + messageHeight + TOP_PADDING + INNER_PADDING + BOTTOM_PADDING

                setMeasuredDimension(totalWidth, totalHeight)
            } else {
                val staticLayout = StaticLayout.Builder
                    .obtain(message, 0, message.length, staticPaint, MAX_WIDTH)
                    .build()

                val totalWidth = max(staticLayout.width, nameWidth) + RIGHT_PADDING + LEFT_PADDING
                val totalHeight =
                    staticLayout.height + nameHeight + TOP_PADDING + INNER_PADDING + BOTTOM_PADDING
                setMeasuredDimension(totalWidth, totalHeight)
            }
        } else setMeasuredDimension(0, 0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        backgroundRect.apply {
            top = 0f
            left = 0f
            right = w.toFloat()
            bottom = h.toFloat()
        }

        textCoordinate.x = w / 2f
        textCoordinate.y = h / 2f + nameBounds.height() / 2 - namePaint.fontMetrics.descent

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            backgroundRect,
            UserMessageLayout.VIEW_BG_RECT_RADIUS,
            UserMessageLayout.VIEW_BG_RECT_RADIUS, backPaint
        )
        if (name.isNotBlank()) {
            canvas.drawText(name, textCoordinate.x, textCoordinate.y, textPaint)
        }
    }

    private fun setMessage(userName: String, message: String) {
        name = userName
    }

    companion object {
        private val LINE_LENGHT = 7.dpToPixels().toFloat()
        private val LINE_WIDTH = 2.dpToPixels().toFloat()
        private val VIEW_BG_RECT_RADIUS = 10.dpToPixels().toFloat()
        private val LEFT_PADDING = 12.dpToPixels()
        private val RIGHT_PADDING = 4.dpToPixels()
        private val TOP_PADDING = 8.dpToPixels()
        private val INNER_PADDING = 4.dpToPixels()
        private val BOTTOM_PADDING = 20.dpToPixels()
        private val MAX_WIDTH = 265.dpToPixels() - RIGHT_PADDING - LEFT_PADDING
        private const val DEFAUL_BG_COLOR = Color.DKGRAY
        private const val CHECK_BG_COLOR = Color.LTGRAY
    }

}