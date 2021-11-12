package com.example.tfs.network.models

import com.example.tfs.domain.streams.StreamItemList
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

@Serializable
data class MessageQueueResponse(
    @SerialName("message")
    val postList: List<Post> = emptyList(),
)

@Serializable
data class Post(
    @SerialName("id")
    val id: Int,
//    @SerialName("is_me_message")
//    val isOwner: Boolean = false,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("sender_full_name")
    val senderName: String,
    @SerialName("content")
    val content: String,
    @SerialName("content_type")
    val content_type: String,
    @SerialName("avatar_url")
    val avatar: String? = null,
    @SerialName("timestamp")
    val timeStamp: Long,
    @SerialName("flags")
    val postStatus: List<String>,
    @SerialName("reactions")
    var reaction: List<PostReaction> = emptyList(),
)

@Serializable
data class PostReaction(
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("reaction_type")
    val type: String,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("user")
    val userInfo: User
)

@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
)

fun Stream.toDomainStream(
    parentName: String,
    topics: List<Topic> = emptyList(),
    isExpanded: Boolean = false,
) = StreamItemList.StreamItem(id,
    name,
    topics = topics.map { it.toDomainTopic(parentName) },
    expanded = isExpanded)

fun Topic.toDomainTopic(parentStream: String) =
    StreamItemList.TopicItem(name = name, parentStream, max_id = max_id)

