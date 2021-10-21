package com.example.tfs.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.tfs.data.Post
import com.example.tfs.data.Reaction
import com.example.tfs.util.dpToPixels
import kotlin.math.max

class PostLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var avatarChild: View? = null
    private var messageChild: View? = null
    private var emojiChild: View? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i("PostLayout", "Function called: onMeasure() checkchild $childCount")
        checkChildCount()

        avatarChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        messageChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        emojiChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }

        Log.i("PostLayout", "ava ${avatarChild?.measuredHeight} message ${messageChild?.measuredHeight} emo ${emojiChild?.measuredHeight}")

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
        if (childCount > 3) error("CustomViewGroup should not contain more than 3 children")
    }

    fun createLayout(data: Post) {
        removeAllViews().also {
            avatarChild = null
            messageChild = null
            emojiChild = null
        }

        var childOffset = 1

        if(data.isOwner) {
            childOffset = 0
            addView(OwnerMessageLayout(context, userMessage = data.message))
            messageChild = getChildAt(0)
        } else {
            addView(avatarChild)
            avatarChild = getChildAt(0)
            addView(UserMessageLayout(context, userMessage = data.message))
            messageChild = getChildAt(1)
        }

        if (data.reaction.any { it.count > 0 }) {
            val params = LayoutParams(VIEW_WIDTH, LayoutParams.WRAP_CONTENT)
            val emojisLayout = EmojisLayout(context)
            emojisLayout.layoutParams = params
            emojisLayout.setReactionData(data.reaction)
            addView(emojisLayout)
            emojiChild = getChildAt(childOffset + 1)
        }

        requestLayout()
    }

    companion object {
        private var CHILD_DIVIDER = 8.dpToPixels()
        private val VIEW_WIDTH = 265.dpToPixels()
    }
}