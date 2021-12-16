package com.example.tfs.domain.stream

import com.example.tfs.database.entity.LocalStream
import com.example.tfs.common.baseadapter.AdapterItem

internal class StreamToUiItemMapper : (List<LocalStream>) -> (List<AdapterItem>) {

    override fun invoke(streams: List<LocalStream>): List<AdapterItem> {
        val domainStreamList = mutableListOf<AdapterItem>()

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

