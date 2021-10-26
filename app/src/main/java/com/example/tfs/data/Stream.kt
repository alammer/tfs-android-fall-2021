package com.example.tfs.data

sealed class StreamCell {

    class StreamNameCell(
        val streamId: Int,
        val streamName: String,
        val childTopics: MutableList<TopicNameCell> = mutableListOf(),
        var expanded: Boolean = false
    ) :
        StreamCell()

    class TopicNameCell(
        val topicId: Int,
        val parentStreamId: Int,
        val topicName: String,
        val messageStat: Int
    ) : StreamCell()
}
