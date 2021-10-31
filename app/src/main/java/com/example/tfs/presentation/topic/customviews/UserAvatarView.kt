package com.example.tfs.presentation.topic.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.tfs.R
import com.example.tfs.util.dpToPixels

//если не инфлэйтим явно, то параметры типа Uri передавать при создании, а статусы и т.п. в конуструктор
@SuppressLint("AppCompatCustomView")
class UserAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ImageView(context, attrs, defStyleAttr, defStyleRes) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private var onlineState: Int? = null
    private var userImage: Int? = null

    private lateinit var statePaint: Paint
    private lateinit var borderStatePaint: Paint
    private lateinit var stateRect: Rect

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }
        prepareShader(w, h)
        onlineState?.let {
            with(stateRect) {
                left = w - w / 3
                top = h - h / 3
                right = w
                bottom = h
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawOval(viewRect.toRectF(), paint)
        onlineState?.let {
            canvas?.drawCircle(
                stateRect.exactCenterX(),
                stateRect.exactCenterY(),
                stateRect.width() / 2f,
                statePaint
            )
            canvas?.drawCircle(
                stateRect.exactCenterX(),
                stateRect.exactCenterY(),
                stateRect.width() / 2f,
                borderStatePaint
            )
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        userImage?.let { avatarUri ->
            ContextCompat.getDrawable(context, avatarUri)?.let {
                val srcBitmap = it.toBitmap(w, h, Bitmap.Config.ARGB_8888)
                paint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
        }
    }

    fun setAvatar(userAvatarUri: Int? = null, userState: Int? = null, userName: String) {
        onlineState = userState
        onlineState?.let {
            statePaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = when (onlineState) {
                    0 -> ContextCompat.getColor(context, R.color.white)
                    else -> ContextCompat.getColor(context, R.color.state_color_green)
                }
            }

            borderStatePaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = ContextCompat.getColor(context, R.color.view_bg)
                strokeWidth = STATE_BORDER_WIDTH
            }
            stateRect = Rect()
        }

        userAvatarUri?.let {
            userImage = it
        }

        requestLayout()
    }

    companion object {
        private val STATE_BORDER_WIDTH = 2.dpToPixels().toFloat()
    }
}