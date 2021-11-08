package com.example.tfs.domain.streams

import com.example.tfs.domain.topic.Reaction
import com.example.tfs.domain.topic.TopicItem

data class RemoteStream(
    val id: Int,
    val name: String,
    val description: String? = null,
    val isPinToTop: Boolean = false,
    val role: Int = 20,
    val subscribers: List<Int> = emptyList(),
    val isPushNotification: Boolean = false,
    val color: String? = null,
    val emailAddress: String? = null,
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
    val content: String,
    val avatar: String? = null,
    val timeStamp: Long,
    val type: String? = null,
)

fun RemoteTopic.toDomainTopic() =
    StreamItemList.TopicItem(
        id = 0,
        name = name,
        parentStreamName = parentStreamName,
        messageStat = topic_stat
    )

fun RemoteStream.toDomainStream() =
    StreamItemList.StreamItem(id, name)

fun Post.toDomainOwnerPost() =
    TopicItem.OwnerPostItem(id = id, reaction = reaction, message = content, timeStamp = timeStamp)

fun Post.toDomainUserPost() =
    TopicItem.UserPostItem(
        id,
        userName = senderName,
        userId = senderId,
        reaction = reaction,
        message = content,
        avatar = avatar,
        timeStamp = timeStamp
    )
