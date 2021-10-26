package com.example.tfs.ui.topic

interface TopicAdapterCallback {

    fun onRecycleViewItemClick(position: Int, emojiCode: Int)
    fun onRecycleViewLongPress(postPosition: Int)
}