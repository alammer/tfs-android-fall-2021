package com.example.tfs.ui.streams.adapter

import com.example.tfs.database.entity.LocalStream
import com.example.tfs.database.entity.toDomainStream
import com.example.tfs.domain.streams.StreamItemList

internal class StreamToItemMapper : (List<LocalStream>, List<Int>) -> (List<StreamItemList>) {

    override fun invoke(streams: List<LocalStream>, expanded: List<Int>): List<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()

        streams.forEach { stream ->
            domainStreamList.add(stream.toDomainStream(stream.streamId in expanded))
            stream.topics.forEach { topicName ->
                domainStreamList.add(StreamItemList.TopicItem(topicName, stream.streamName ))
            }
        }
        return domainStreamList.toList()
    }
}

