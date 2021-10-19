package com.example.tfs.ui.topic

interface TopicAdapterCallback {
    fun onRecycleViewItemClick(position: Int, emojiPositon: Int)
    fun onRecycleViewLongPress(postPosition: Int)
}