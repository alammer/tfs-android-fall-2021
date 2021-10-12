package com.example.tfs.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.example.tfs.util.dpToPixels

class EmojisLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var rowWidth = 0
        var totalHeight = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (totalHeight == 0) totalHeight = child.measuredHeight
            if (rowWidth + child.measuredWidth <= widthSize ) {
                rowWidth += (child.measuredWidth + DIVIDER_WIDTH)
            } else {
                rowWidth = child.measuredWidth + DIVIDER_WIDTH
                totalHeight += child.measuredHeight + DIVIDER_HEIGHT
            }

            Log.i("EmojisLayout", "Function called: onMeasure() $rowWidth")
        }

        Log.i("EmojisLayout", "Function called: onMeasure() $rowWidth $totalHeight $widthSize $paddingStart $paddingEnd $widthMeasureSpec $heightMeasureSpec")

        setMeasuredDimension(widthSize, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentBottom = 0
        var currentWidth = 0

        val maxWidth = width
//        Log.i("EmojisLayout", "Function called: onLayout() maxWidth = ${maxWidth.PixelsToDp()}")
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentWidth + child.measuredWidth  >  maxWidth) {
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

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    fun setReactionData(data: List<Reaction>) {
        addReaction(data)
    }

    private fun addReaction(data: List<Reaction>) {
        data.forEach {

            val view = EmojiView(
                context,
                emoji = "😄",
                count = it.count
            )

            view.layoutParams.width = LayoutParams.WRAP_CONTENT
            view.layoutParams.height = 30

            addView(view)
        }
        requestLayout()
    }

    companion object {
        //private var INTERVAL = 5.DpToPixels().toFloat()
        private var DIVIDER_HEIGHT = 8.dpToPixels()
        private var DIVIDER_WIDTH = 10.dpToPixels()
    }
}
