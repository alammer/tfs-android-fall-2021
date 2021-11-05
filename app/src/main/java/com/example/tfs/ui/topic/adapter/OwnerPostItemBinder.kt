package com.example.tfs.ui.topic.adapter

import com.example.tfs.domain.topic.TopicItem

class OwnerPostItemBinder() {

    fun bind(ownerPostViewHolder: OwnerPostViewHolder, item: TopicItem.OwnerPostItem) {

        ownerPostViewHolder.setMessageText(item.message)

        if (item.reaction.isNotEmpty()) ownerPostViewHolder.createPostReaction(item.reaction)
    }
}
