package com.example.tfs.domain.streams

import com.example.tfs.database.entity.LocalStream
import com.example.tfs.database.entity.toDomainStream

internal class StreamToItemMapper : (List<LocalStream>) -> (List<StreamListItem>) {

    override fun invoke(streams: List<LocalStream>): List<StreamListItem> {
        val domainStreamList = mutableListOf<StreamListItem>()

        streams.forEach { stream ->
            domainStreamList.add(stream.toDomainStream())
            stream.topics.forEach { topicName ->
                domainStreamList.add(StreamListItem.TopicItem(topicName, stream.streamName ))
            }
        }
        return domainStreamList.toList()
    }
}

