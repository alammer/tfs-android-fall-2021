package com.example.tfs.presentation.topic

interface TopicAdapterCallback {

    fun onRecycleViewItemClick(position: Int, emojiCode: Int)
    fun onRecycleViewLongPress(postPosition: Int)
}