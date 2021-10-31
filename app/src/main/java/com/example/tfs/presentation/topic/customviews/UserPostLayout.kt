package com.example.tfs.presentation.topic.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.tfs.data.Reaction
import com.example.tfs.data.TopicItem
import com.example.tfs.util.dpToPixels
import kotlin.math.max

class UserPostLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var avatarChild: View? = null
    private var messageChild: View? = null
    private var emojiChild: View? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        checkChildCount()

        avatarChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        messageChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        emojiChild?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }

        val avatarHeight = avatarChild?.measuredHeight ?: 0
        val messageHeight = messageChild?.measuredHeight ?: 0
        val emojiHeight = emojiChild?.measuredHeight ?: 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        val height = max(
            avatarHeight,
            messageHeight + emojiHeight + CHILD_DIVIDER
        )

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

        val startPost =
            (avatarChild?.measuredWidth ?: 0) + CHILD_DIVIDER + paddingStart


        val endPost =
            paddingStart + (avatarChild?.measuredWidth
                ?: 0) + CHILD_DIVIDER + (messageChild?.measuredWidth ?: 0)

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
        if (childCount > 3) error("CustomViewGroup should not contain more than 3 children")
    }

    fun createChildViews(data: TopicItem.UserPostItem) {
        removeAllViews().also {
            avatarChild = null
            messageChild = null
            emojiChild = null
        }

        val avatarView = UserAvatarView(context)
        avatarView.layoutParams = LayoutParams(AVATAR_VIEW_WIDTH, AVARTAR_VIEW_HEIGHT)
        avatarView.setAvatar(0, data.avatar, userName = "Ivan Ivanov")
        addView(avatarView)
        avatarChild = getChildAt(0)
        addView(UserMessageLayout(context, userMessage = data.message))
        messageChild = getChildAt(1)


        addView(createEmojiLayout(data.reaction))

        emojiChild = getChildAt(2)

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
                isClicked = it.isClicked
            )
            view.layoutParams = childLayoutParams
            emojisLayout.addView(view)
        }

        if (reactionData.isNotEmpty()) {
            val plusView = PlusView(emojisLayout.context)
            plusView.layoutParams = childLayoutParams
            emojisLayout.addView(plusView)
        }

        return emojisLayout
    }

    companion object {
        private val CHILD_HEIGHT = 30.dpToPixels()
        private val CHILD_DIVIDER = 8.dpToPixels()
        private val VIEW_WIDTH = 265.dpToPixels()
        private val AVATAR_VIEW_WIDTH = 37.dpToPixels()
        private val AVARTAR_VIEW_HEIGHT = 37.dpToPixels()
    }
}