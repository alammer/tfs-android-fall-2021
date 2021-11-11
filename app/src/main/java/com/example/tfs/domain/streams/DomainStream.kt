package com.example.tfs.domain.streams

sealed class StreamItemList {

    data class StreamItem(
        val id: Int,
        val name: String,
        val topics: List<TopicItem> = emptyList(),
        val expanded: Boolean = false,
    ) : StreamItemList()

    data class TopicItem(
        val name: String,
        val parentStreamName: String,
        val max_id: Int
    ) : StreamItemList()
}
