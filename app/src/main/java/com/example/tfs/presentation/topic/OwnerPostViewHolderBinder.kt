package com.example.tfs.presentation.topic

import com.example.tfs.data.TopicItem

class OwnerPostViewHolderBinder() {

    fun bind(ownerPostViewHolder: OwnerPostViewHolder, item: TopicItem.OwnerPostItem) {
        val itemView = ownerPostViewHolder.ownerPostView
        itemView.createLayout(item)
    }
}