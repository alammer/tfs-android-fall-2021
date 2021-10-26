package com.example.tfs.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.tfs.R
import com.example.tfs.util.dpToPixels

@SuppressLint("AppCompatCustomView")
class UserAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    avatarUri: Int? = null
) : ImageView(context, attrs, defStyleAttr, defStyleRes) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private var initials = "UK"
    private var userImage: Int? = null

    init {
        attrs?.let {
            val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.UserAvatarView,
                defStyleAttr,
                defStyleRes
            )
            initials = typedArray.getString(R.styleable.UserAvatarView_uav_initials) ?: "UK"
            typedArray.recycle()
        }
        userImage = avatarUri
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(SIZE, SIZE)
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
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawOval(viewRect.toRectF(), paint)
    }

    private fun prepareShader(w: Int, h: Int) {
        userImage?.let { avatarUri ->
            ContextCompat.getDrawable(context, avatarUri)?.let {
                val srcBitmap = it.toBitmap(w, h, Bitmap.Config.ARGB_8888)
                paint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
        }
    }

    companion object {
        private var SIZE = 37.dpToPixels()
    }
}