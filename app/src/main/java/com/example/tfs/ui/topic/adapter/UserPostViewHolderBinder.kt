package com.example.tfs.ui.topic.adapter

import android.view.ViewGroup
import com.example.tfs.domain.topic.Reaction
import com.example.tfs.domain.TopicItem
import com.example.tfs.ui.topic.customviews.EmojiView
import com.example.tfs.ui.topic.customviews.PlusView
import com.example.tfs.util.dpToPixels

class UserPostViewHolderBinder(
    private val onChangeReactionClick: (Int, Int) -> Unit,
    private val onAddReactionClick: (messageId: Int) -> Unit
) {

    fun bind(userPostViewHolder: UserPostViewHolder, item: TopicItem.UserPostItem) {

        userPostViewHolder.setUserName(item.userName)

        userPostViewHolder.setMessageText(item.message)

        userPostViewHolder.setMessageClickListener(onAddReactionClick, item.messageId)

        item.avatar?.let {
            userPostViewHolder.setUserAvatarImage(it)
        } ?: userPostViewHolder.setUserInitilas(item.userName)

        if (item.reaction.isNotEmpty()) {
            val emojiGroup = userPostViewHolder.getEmojilayout()

            createEmojiLayout(emojiGroup, item.reaction)

            (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
                emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                    onChangeReactionClick(item.messageId, it.tag as Int)
                }
            }
            //click on "+"
            emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
                onAddReactionClick(item.messageId)
            }
        }
    }

    private fun createEmojiLayout(emojiGroup: ViewGroup, reaction: List<Reaction>) {

        val childLayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            CHILD_HEIGHT
        )

        reaction.forEach {
            val view = EmojiView(
                emojiGroup.context,
                emojiCode = it.emoji,
                count = it.count,
                isClicked = it.isClicked
            )
            view.tag = it.emoji
            view.layoutParams = childLayoutParams
            emojiGroup.addView(view)
        }

        if (reaction.isNotEmpty()) {
            val plusView = PlusView(emojiGroup.context)
            plusView.layoutParams = childLayoutParams
            emojiGroup.addView(plusView)
        }
    }

    companion object {
        private val CHILD_HEIGHT = 30.dpToPixels()
        private val VIEW_WIDTH = 265.dpToPixels()
    }
}
