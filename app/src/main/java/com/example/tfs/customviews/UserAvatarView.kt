package com.example.tfs.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.tfs.R


@SuppressLint("AppCompatCustomView")
class UserAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ImageView(context, attrs, defStyleAttr, defStyleRes) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private lateinit var avatarBitmap : Bitmap
    private lateinit var maskBitmap : Bitmap
    private lateinit var srcBitmap : Bitmap
    private var initials = "UK"

    init {
        attrs?.let {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.UserAvatarView,
                defStyleAttr,
                defStyleRes
            )

            initials = typedArray.getString(R.styleable.UserAvatarView_uav_initials) ?: "UK"
//            text = typedArray.getString(R.styleable.CustomTextView_customText).orEmpty()
//            textPaint.color =
//                typedArray.getColor(R.styleable.CustomTextView_customTextColor, Color.BLACK)
            typedArray.recycle()
        }

        setup()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
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

        prepareBitmaps(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(avatarBitmap, viewRect, viewRect, null)
    }

    private fun setup() {
        scaleType = ScaleType.CENTER_CROP

        with(paint) {
            color = Color.BLUE
            style = Paint.Style.FILL
        }
    }

    private fun prepareBitmaps(w: Int, h: Int) {
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        avatarBitmap = maskBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val maskCanvas = Canvas(maskBitmap)
        maskCanvas.drawOval(viewRect.toRectF(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)


        val avatarCanvas = Canvas(avatarBitmap)
        avatarCanvas.drawBitmap(maskBitmap, viewRect, viewRect, null)
        drawable?.let {
            srcBitmap = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
            avatarCanvas.drawBitmap(srcBitmap, viewRect, viewRect, paint)
        }
    }
}