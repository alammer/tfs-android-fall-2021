package com.example.tfs.data

sealed class StreamItemList {

    data class StreamItem(
        val streamId: Int,
        val streamName: String,
        val childTopics: List<TopicItem> = emptyList(),
        val expanded: Boolean = false,
    ) : StreamItemList()

    data class TopicItem(
        val topicId: Int,
        val topicName: String,
        val parentStreamId: Int,
        val parentStreamName: String,
        val messageStat: Int
    ) : StreamItemList()
}
