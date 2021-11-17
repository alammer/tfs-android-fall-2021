package com.example.tfs.domain.streams

sealed class StreamItemList {

    data class StreamItem(
        val id: Int,
        val name: String,
        val topics: List<String> = emptyList(),
        val expanded: Boolean = false,
    ) : StreamItemList()

    data class TopicItem(
        val name: String,
        val parentStreamName: String,
    ) : StreamItemList()
}
