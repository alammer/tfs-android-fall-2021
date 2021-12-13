package com.example.tfs.domain.streams

import com.example.tfs.database.entity.LocalStream
import com.example.tfs.ui.stream.adapter.base.StreamListItem

internal class StreamToItemMapper : (List<LocalStream>) -> (List<StreamListItem>) {

    override fun invoke(streams: List<LocalStream>): List<StreamListItem> {
        val domainStreamList = mutableListOf<StreamListItem>()

        streams.forEach { stream ->
            domainStreamList.add(stream.toDomainStream())
            if (stream.isExpanded) {
                stream.topics.forEach { topicName ->
                    domainStreamList.add(DomainTopic(topicName, stream.streamName))
                }
            }
        }
        return domainStreamList.toList()
    }
}

fun LocalStream.toDomainStream() =
    DomainStream(id = streamId,
        name = "#$streamName",
        topics = topics,
        expanded = isExpanded)

