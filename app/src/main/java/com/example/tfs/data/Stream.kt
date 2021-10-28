package com.example.tfs.data

sealed class StreamCell {

    data class StreamItemCell(
        val streamId: Int,
        val streamName: String,
        val childTopics: MutableList<TopicItemCell> = mutableListOf(),
        var expanded: Boolean = false
    ) : StreamCell()

    data class TopicItemCell(
        val topicId: Int,
        val parentStreamId: Int,
        val topicName: String,
        val messageStat: Int
    ) : StreamCell()
}
