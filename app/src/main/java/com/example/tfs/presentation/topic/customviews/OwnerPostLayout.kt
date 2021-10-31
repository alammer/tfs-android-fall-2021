package com.example.tfs.presentation.topic.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.tfs.data.Reaction
import com.example.tfs.data.TopicItem
import com.example.tfs.util.dpToPixels

class OwnerPostLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var messageChild: View? = null
    private var emojiChild: View? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        checkChildCount()

        messageChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        emojiChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }

        val messageHeight = messageChild?.measuredHeight ?: 0
        val emojiHeight = emojiChild?.measuredHeight ?: 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        val height = messageHeight + emojiHeight + CHILD_DIVIDER

        setMeasuredDimension(
            widthSize + paddingStart + paddingEnd,
            height + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val startPost =
            r - (messageChild?.measuredWidth ?: 0) - paddingEnd - CHILD_DIVIDER - paddingStart


        val endPost = r - CHILD_DIVIDER - paddingEnd - paddingStart

        messageChild?.layout(
            startPost,
            paddingTop,
            endPost,
            paddingTop + (messageChild?.measuredHeight ?: 0)
        )
        emojiChild?.layout(
            startPost,
            paddingTop + (messageChild?.measuredHeight ?: 0) + CHILD_DIVIDER,
            endPost,
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
        if (childCount > 2) error("CustomViewGroup should not contain more than 3 children")
    }

    fun createLayout(data: TopicItem.OwnerPostItem) {
        removeAllViews().also {
            messageChild = null
            emojiChild = null
        }

        addView(OwnerMessageLayout(context, userMessage = data.message))
        messageChild = getChildAt(0)

        addView(createEmojiLayout(data.reaction))

        emojiChild = getChildAt(1)

        requestLayout()
    }

    private fun createEmojiLayout(reactionData: List<Reaction>): ViewGroup {
        val rootLayoutParams = LayoutParams(VIEW_WIDTH, LayoutParams.WRAP_CONTENT)
        val emojisLayout = EmojisLayout(context)
        emojisLayout.layoutParams = rootLayoutParams

        val childLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, CHILD_HEIGHT)

        reactionData.forEach {
            val view = EmojiView(
                emojisLayout.context,
                emojiCode = it.emoji,
                count = it.count,
            )
            view.tag = it.emoji
            view.layoutParams = childLayoutParams
            emojisLayout.addView(view)
        }

        return emojisLayout
    }

    companion object {
        private val CHILD_HEIGHT = 30.dpToPixels()
        private val CHILD_DIVIDER = 8.dpToPixels()
        private val VIEW_WIDTH = 265.dpToPixels()
    }
}