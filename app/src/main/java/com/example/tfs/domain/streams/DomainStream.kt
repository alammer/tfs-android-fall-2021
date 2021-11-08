package com.example.tfs.domain.streams

sealed class StreamItemList {

    data class StreamItem(
        val id: Int,
        val name: String,
        val expanded: Boolean = false,
    ) : StreamItemList()

    data class TopicItem(
        val id: Int,
        val name: String,
        val parentStreamName: String,
        val messageStat: Int
    ) : StreamItemList()
}
