package com.example.tfs.ui.topic.adapter

import android.view.ViewGroup
import com.example.tfs.domain.topic.Reaction
import com.example.tfs.domain.TopicItem
import com.example.tfs.ui.topic.customview.EmojiView
import com.example.tfs.util.dpToPixels

class OwnerPostViewHolderBinder() {

    fun bind(ownerPostViewHolder: OwnerPostViewHolder, item: TopicItem.OwnerPostItem) {

        ownerPostViewHolder.setMessageText(item.message)

        if (item.reaction.isNotEmpty()) {
            val emojiGroup = ownerPostViewHolder.getEmojilayout()
            createEmojiLayout(emojiGroup, item.reaction)
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
            )
            view.layoutParams = childLayoutParams
            emojiGroup.addView(view)
        }
    }

    companion object {
        private val CHILD_HEIGHT = 30.dpToPixels()
    }
}