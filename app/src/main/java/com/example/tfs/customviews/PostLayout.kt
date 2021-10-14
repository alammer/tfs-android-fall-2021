package com.example.tfs.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.toast
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

        val avatarHeight = avatarChild?.measuredHeight ?: 0
        val messageHeight = messageChild?.measuredHeight ?: 0
        val emojiHeight = emojiChild?.measuredHeight ?: 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        val height = max(avatarHeight, messageHeight + emojiHeight + CHILD_DIVIDER)

        setMeasuredDimension(
            widthSize + paddingStart + paddingEnd,
            height + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatarChild?.layout(
            paddingStart,
            paddingTop,
            paddingStart + (avatarChild?.measuredWidth ?: 0),
            paddingTop + (avatarChild?.measuredHeight ?: 0)
        )
        messageChild?.layout(
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER + paddingStart,
            paddingTop,
            paddingStart + (avatarChild?.measuredWidth
                ?: 0) + CHILD_DIVIDER + (messageChild?.measuredWidth ?: 0),
            paddingTop + (messageChild?.measuredHeight ?: 0)
        )
        emojiChild?.layout(
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER + paddingStart,
            paddingTop + (messageChild?.measuredHeight ?: 0) + CHILD_DIVIDER,
            paddingStart + (avatarChild?.measuredWidth
                ?: 0) + CHILD_DIVIDER + (emojiChild?.measuredWidth ?: 0),
            paddingTop + (messageChild?.measuredHeight
                ?: 0) + CHILD_DIVIDER + (emojiChild?.measuredHeight ?: 0)
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
        if (childCount > 3) error("CustomViewGroup should not contain more than 2 children")
    }

    fun createLayout() {
        if (childCount > 1) {
            removeViewAt(2)
            removeViewAt(1)
            requestLayout()
            context.toast("try to update custom group")
        }

        val dataSet = List((0..25).random()) { Reaction(START_CODE_POINT + (0..40).random(), (0..1000).random()) }

        val testMessage = """
hi if are you new in android use this way Apply your view to make it gone GONE is one way, else, get hold of the parent view, and remove the child from there..... else get the parent layout and use this method an remove all child parentView.remove(child)

I would suggest using the GONE approach...
"""

        val messageView = UserMessageLayout(context, userMessage = testMessage.substring((1..testMessage.length).random()))
        addView(messageView)

        val params = LayoutParams(VIEW_WIDTH, LayoutParams.WRAP_CONTENT)
        val emojisLayout = EmojisLayout(context)
        emojisLayout.layoutParams = params
        emojisLayout.setReactionData(dataSet)
        addView(emojisLayout)

        requestLayout()
    }

    companion object {
        private const val START_CODE_POINT = 0x1f600
        private var CHILD_DIVIDER = 8.dpToPixels()
        private val VIEW_WIDTH = 265.dpToPixels()
    }


}