package com.example.tfs.domain.streams

sealed class StreamItemList {

    data class StreamItem(
        val id: Int,
        val name: String,
        val expanded: Boolean,
    ) : StreamItemList()

    data class TopicItem(
        val name: String,
        val parentStreamName: String,
        val max_id: Int
    ) : StreamItemList()
}
