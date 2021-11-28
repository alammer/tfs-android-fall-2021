package com.example.tfs.domain.streams

sealed class StreamListItem {

    data class StreamItem(
        val id: Int,
        val name: String,
        val topics: List<String> = emptyList(),
        val expanded: Boolean = false,
    ) : StreamListItem()

    data class TopicItem(
        val name: String,
        val parentStreamName: String,
    ) : StreamListItem()
}
