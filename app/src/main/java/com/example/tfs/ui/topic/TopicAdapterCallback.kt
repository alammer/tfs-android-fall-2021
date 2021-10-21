package com.example.tfs.ui.topic

interface TopicAdapterCallback {
    fun onRecycleViewItemClick(position: Int, emojiPosition: Int)
    fun onRecycleViewLongPress(postPosition: Int)
}