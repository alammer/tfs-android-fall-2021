package com.example.tfs.presentation.topic

import android.view.ViewGroup
import com.example.tfs.data.TopicItem

class UserPostViewHolderBinder(
    private val onChangeReactionClick: (TopicItem.UserPostItem, Int) -> Unit,
    private val onAddReactionClick: (TopicItem.UserPostItem) -> Unit
) {

    fun bind(userPostViewHolder: UserPostViewHolder, item: TopicItem.UserPostItem) {

        val itemParentView = userPostViewHolder.getViewHolderRootLayout()

        itemParentView.createChildViews(item)

        itemParentView.getChildAt(1).setOnLongClickListener {
            onAddReactionClick(item)
            return@setOnLongClickListener true
        }

        itemParentView.getChildAt(2)?.let { emojiGroup ->
            if (emojiGroup is ViewGroup && emojiGroup.childCount > 1) {
                (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
                    emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                        onChangeReactionClick(item, it.tag as Int)
                    }
                }
                //click on "+"
                emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
                    onAddReactionClick(item)
                }
            }
        }
    }
}