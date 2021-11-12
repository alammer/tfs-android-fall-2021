package com.example.tfs.ui.topic.adapter

import com.example.tfs.domain.topic.PostItem

class OwnerPostItemBinder() {

    fun bind(ownerPostViewHolder: OwnerPostViewHolder, item: PostItem.OwnerPostItem) {

        ownerPostViewHolder.setMessageText(item.message)

        if (item.reaction.isNotEmpty()) ownerPostViewHolder.createPostReaction(item.reaction)
    }
}
