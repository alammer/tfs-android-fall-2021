package com.example.tfs.network.models

import com.example.tfs.database.entity.LocalStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawStreamResponse(
    val streams: List<Stream>,
)

@Serializable
data class SubscribedStreamResponse(
    @SerialName(value = "subscriptions")
    val streams: List<Stream>,
)

@Serializable
data class Stream(
    @SerialName("stream_id")
    val id: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class TopicResponse(
    @SerialName("topics")
    val topicResponseList: List<Topic>,
)

@Serializable
data class Topic(
    @SerialName("max_id")
    val max_id: Int,
    @SerialName("name")
    val name: String,
)

fun Stream.toLocalStream(
    isSubscribed: Boolean = false,
    topics: List<Topic> = emptyList(),
) = LocalStream(
    id,
    name,
    isSubscribed = isSubscribed,
    topics = topics.map { it.name },
)





