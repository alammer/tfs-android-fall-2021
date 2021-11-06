package com.example.tfs.domain.streams

import com.example.tfs.domain.topic.TopicItem
import com.example.tfs.domain.topic.Reaction

data class RemoteStream(
    val id: Int,
    val name: String,
    val description: String?,
    val isPinToTop: Boolean = false,
    val role: Int = 20,
    val subscribers: List<Int> = emptyList(),
    val isPushNotification: Boolean = false,
    val color: String?,
    val emailAddress: String?,
    val isMute: Boolean = false,
    val streamTraffic: Int = 0,
    val streamPostPolicy: Int = 1,
)

data class RemoteTopic(
    val name: String,
    val parentStreamName: String,
    val topic_stat: Int,
    val postList: List<Post>,
)

data class Post(
    val id: Int,
    val isOwner: Boolean = false,
    val senderId: Int,
    val senderName: String,
    var reaction: List<Reaction> = emptyList(),
    val message: String,
    val avatar: String? = null,
    val timeStamp: Long,
    val type: String? = null,
)

fun RemoteTopic.toDomainTopic() =
    StreamItemList.TopicItem(id = 0, name = name, parentStreamName = parentStreamName, messageStat = topic_stat)

fun RemoteStream.toDomainStream() =
    StreamItemList.StreamItem(id, name)

fun Post.toDomainOwnerPost() =
    TopicItem.OwnerPostItem(id  = id, reaction = reaction, message = message, timeStamp = timeStamp)

fun Post.toDomainUserPost() =
    TopicItem.UserPostItem(id, userName = senderName, userId = senderId, reaction = reaction, message =  message, avatar = avatar, timeStamp = timeStamp)
