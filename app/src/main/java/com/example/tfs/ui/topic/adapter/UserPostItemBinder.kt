package com.example.tfs.ui.topic.adapter

import com.example.tfs.domain.topic.PostItem

class UserPostItemBinder(
    private val onChangeReactionClick: (Int, Int) -> Unit,
    private val onAddReactionClick: (messageId: Int) -> Unit
) {

    fun bind(userPostViewHolder: UserPostViewHolder, item: PostItem.UserPostItem) {

        userPostViewHolder.setUserName(item.userName)

        userPostViewHolder.setMessageText(item.message)

        userPostViewHolder.setMessageClickListener(item.id, onAddReactionClick)

        item.avatar?.let {
            userPostViewHolder.setUserAvatarImage(it)
        } ?: userPostViewHolder.setUserInitilas(item.userName)

        if (item.reaction.isNotEmpty()) {
            userPostViewHolder.createPostReaction(item.reaction)
            userPostViewHolder.addReactionListeners(item.id, onChangeReactionClick, onAddReactionClick)
        }

    }
}
