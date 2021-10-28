package com.example.tfs.data

sealed class StreamListItem {

    data class StreamItem(
        val streamId: Int,
        val streamName: String,
        val childTopics: MutableList<TopicItem> = mutableListOf(),
        var expanded: Boolean = false
    ) : StreamListItem()

    data class TopicItem(
        val topicId: Int,
        val parentStreamId: Int,
        val topicName: String,
        val messageStat: Int
    ) : StreamListItem()
}
