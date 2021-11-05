package com.example.tfs.domain.streams

import com.example.tfs.domain.topic.TopicItem
import com.example.tfs.domain.topic.Reaction

data class RemoteStream(
    val id: Int,
    val name: String,
    val childTopics: List<RemoteTopic>,
)

data class RemoteTopic(
    val id: Int,
    val name: String,
    val parentStreamId: Int,
    val parentStreamName: String,
    val topic_stat: Int,
    val postList: List<Post>,
)

data class Post(
    val id: Int,
    val userId: Int,
    val userName: String,
    var reaction: List<Reaction> = emptyList(),
    val message: String,
    val avatar: Int? = null,
    val timeStamp: Long,
)

fun RemoteTopic.toDomainTopic() =
    StreamItemList.TopicItem(id, name, parentStreamId, parentStreamName, topic_stat)

fun RemoteStream.toDomainStream() =
    StreamItemList.StreamItem(id, name, childTopics.map { it.toDomainTopic() })

fun Post.toDomainOwnerPost() =
    TopicItem.OwnerPostItem(id  = id, reaction = reaction, message = message, timeStamp = timeStamp)

fun Post.toDomainUserPost() =
    TopicItem.UserPostItem(id, userId, userName, reaction, message, avatar, timeStamp)
