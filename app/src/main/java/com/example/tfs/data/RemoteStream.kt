package com.example.tfs.data

data class RemoteStream(
    val streamId: Int,
    val streamName: String,
    val childTopics: List<RemoteTopic>,
)

data class RemoteTopic(
    val topicId: Int,
    val topicName: String,
    val parentStreamId: Int,
    val topic_stat: Int,
    val postList: List<Post>,
)

data class Post(
    val userId: Int,
    val userName: String,
    var reaction: List<Reaction>? = null,
    val message: String,
    var avatar: Int? = null,
    var timeStamp: Long,
)

fun RemoteStream.toDomainStream() = StreamItemList.StreamItem(streamId, streamName, expanded = false)

fun Post.toDomainOwnerPost() = TopicItem.OwnerPostItem(reaction = reaction, message = message, timeStamp = timeStamp)

fun Post.toDomainUserPost() = TopicItem.UserPostItem(userId, userName, reaction, message, avatar, timeStamp)
