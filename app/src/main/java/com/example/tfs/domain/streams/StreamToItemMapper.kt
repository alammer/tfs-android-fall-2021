package com.example.tfs.domain.streams

import com.example.tfs.database.entity.LocalStream
import com.example.tfs.database.entity.toDomainStream

internal class StreamToItemMapper : (List<LocalStream>) -> (List<StreamItemList>) {

    override fun invoke(streams: List<LocalStream>): List<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()

        streams.forEach { stream ->
            domainStreamList.add(stream.toDomainStream())
            stream.topics.forEach { topicName ->
                domainStreamList.add(StreamItemList.TopicItem(topicName, stream.streamName ))
            }
        }
        return domainStreamList.toList()
    }
}

