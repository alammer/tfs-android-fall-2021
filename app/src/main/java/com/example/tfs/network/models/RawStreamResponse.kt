package com.example.tfs.network.models

import com.example.tfs.domain.streams.StreamItemList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawStreamResponse(
    val streams: List<Stream>?
)

@Serializable
data class SubscribedStreamResponse(
    @SerialName(value = "subscriptions")
    val streams: List<Stream>?
)

@Serializable
data class Stream(
    @SerialName("stream_id")
    val stream_id: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class TopicResponse(
    @SerialName("topics")
    val topicResponseList: List<Topic>?,
)

@Serializable
data class Topic(
    @SerialName("max_id")
    val max_id: Int,
    @SerialName("name")
    val name: String,
)

fun Stream.toDomainStream() = StreamItemList.StreamItem(stream_id, name, false)

fun Topic.toDomainTopic(parentStream: String) = StreamItemList.TopicItem(name = name, parentStream, max_id = max_id)