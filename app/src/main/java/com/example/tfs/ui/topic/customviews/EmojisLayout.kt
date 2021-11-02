package com.example.tfs.ui.topic.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import com.example.tfs.R
import com.example.tfs.util.dpToPixels

class EmojisLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var isOwner = false

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.EmojisLayout,
            defStyleAttr,
            defStyleRes
        )
        isOwner = typedArray.getBoolean(R.styleable.EmojisLayout_el_owner, false)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var rowWidth = 0
        var totalHeight = 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (totalHeight == 0) totalHeight = child.measuredHeight
            if (rowWidth + child.measuredWidth <= widthSize) {
                rowWidth += (child.measuredWidth + DIVIDER_WIDTH)
            } else {
                rowWidth = child.measuredWidth + DIVIDER_WIDTH
                totalHeight += child.measuredHeight + DIVIDER_HEIGHT
            }
        }
        setMeasuredDimension(widthSize, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentBottom = 0
        var currentWidth = if (isOwner) width else 0
        val maxWidth = width

        if (isOwner) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (currentWidth - child.measuredWidth < 0) {
                    currentWidth = width
                    currentBottom += (child.measuredHeight + DIVIDER_HEIGHT)
                }
                child.layout(
                    currentWidth - child.measuredWidth,
                    currentBottom,
                    currentWidth,
                    currentBottom + child.measuredHeight
                )
                currentWidth -= (child.measuredWidth + DIVIDER_WIDTH)
            }
        } else {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (currentWidth + child.measuredWidth > maxWidth) {
                    currentWidth = 0
                    currentBottom += (child.measuredHeight + DIVIDER_HEIGHT)
                }
                child.layout(
                    currentWidth,
                    currentBottom,
                    currentWidth + child.measuredWidth,
                    currentBottom + child.measuredHeight
                )
                currentWidth += (child.measuredWidth + DIVIDER_WIDTH)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    companion object {
        private var DIVIDER_HEIGHT = 8.dpToPixels()
        private var DIVIDER_WIDTH = 10.dpToPixels()
    }
}
