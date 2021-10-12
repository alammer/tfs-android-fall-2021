package com.example.tfs.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.tfs.util.dpToPixels
import kotlin.math.max

class PostLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val avatarChild: View?
        get() = if (childCount > 0) getChildAt(0) else null
    private val messageChild: View?
        get() = if (childCount > 1) getChildAt(1) else null
    private val emojiChild: View?
        get() = if (childCount > 2) getChildAt(2) else null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        checkChildCount()

        avatarChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        messageChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        emojiChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }

        val avatarWidth = avatarChild?.measuredWidth ?: 0
        val avatarHeight = avatarChild?.measuredHeight ?: 0
        val messageWidth = messageChild?.measuredWidth ?: 0
        val messageHeight = messageChild?.measuredHeight ?: 0
        val emojiWidth = emojiChild?.measuredWidth ?: 0
        val emojiHeight = emojiChild?.measuredHeight ?: 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd

        val height = max(avatarHeight, messageHeight + emojiHeight)

        setMeasuredDimension(widthSize + paddingLeft + paddingRight, height + paddingLeft + paddingRight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatarChild?.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + (avatarChild?.measuredWidth ?: 0),
            paddingTop + (avatarChild?.measuredHeight ?: 0)
        )
        messageChild?.layout(
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER + paddingLeft,
            paddingTop,
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER +  (messageChild?.measuredWidth ?: 0)- paddingRight,
            paddingTop + (messageChild?.measuredHeight ?: 0)
        )
        emojiChild?.layout(
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER + paddingLeft,
            paddingTop + (messageChild?.measuredHeight ?: 0) + CHILD_DIVIDER,
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER +  (emojiChild?.measuredWidth ?: 0) - paddingRight,
            paddingTop + (messageChild?.measuredHeight ?: 0) + CHILD_DIVIDER + (emojiChild?.measuredHeight ?: 0) - paddingBottom
        )
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

    private fun checkChildCount() {
        Log.i("PostLayout", "Function called: checkChildCount()")
        if (childCount > 3) error("CustomViewGroup should not contain more than 2 children")
    }

    fun createLayout() {
        val dataSet = List<Reaction>(26) { i -> Reaction(('A' + i).toString(), i) }

        val emojisLayout = EmojisLayout(context)
        //val messageView = UserMessageLayout(context)

        val params = LayoutParams(265.dpToPixels(), LayoutParams.WRAP_CONTENT)
        emojisLayout.setLayoutParams(params)



        emojisLayout.setReactionData(dataSet)

        addView(emojisLayout)
        requestLayout()
    }

    companion object {
        private var CHILD_DIVIDER = 8.dpToPixels()
        //private var DIVIDER_WIDTH = 8.dpToPixels()
    }



}