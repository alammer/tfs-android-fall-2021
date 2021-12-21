package com.example.tfs.domain.stream

import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.database.entity.LocalStream

internal class StreamToUiItemMapper : (List<LocalStream>) -> (List<AdapterItem>) {

    override fun invoke(streams: List<LocalStream>): List<AdapterItem> {

        if (streams.isEmpty()) return emptyList()

        val domainStreamList = mutableListOf<AdapterItem>()

        streams.forEach { stream ->
            domainStreamList.add(stream.toDomainStream())
            if (stream.isExpanded) {
                stream.topics.forEach { topicName ->
                    domainStreamList.add(
                        RelatedTopic(
                            topicName,
                            stream.streamName,
                            stream.streamId
                        )
                    )
                }
            }
        }
        return domainStreamList.toList()
    }
}

fun LocalStream.toDomainStream() =
    DomainStream(
        id = streamId,
        name = "#$streamName",
        topics = topics,
        expanded = isExpanded
    )

