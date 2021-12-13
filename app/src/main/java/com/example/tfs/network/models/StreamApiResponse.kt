package com.example.tfs.network.models

import com.example.tfs.database.entity.LocalStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllStreamResponse(
    @SerialName(value = "streams")
    val allStreams: List<RemoteStream>,
)

@Serializable
data class SubscribedStreamResponse(
    @SerialName(value = "subscriptions")
    val subscribedStreams: List<RemoteStream>,
)

@Serializable
data class RemoteStream(
    @SerialName("stream_id")
    val id: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class TopicResponse(
    @SerialName("topics")
    val remoteTopicResponseList: List<RemoteTopic>,
)

@Serializable
data class RemoteTopic(
    @SerialName("max_id")
    val max_id: Int,
    @SerialName("name")
    val name: String,
)

fun RemoteStream.toLocalStream(
    isSubscribed: Boolean = false,
    isExpanded: Boolean = false,
    remoteTopics: List<RemoteTopic> = emptyList(),
) = LocalStream(
    id,
    name,
    isSubscribed = isSubscribed,
    isExpanded = isExpanded,
    topics = remoteTopics.map { it.name },
)





