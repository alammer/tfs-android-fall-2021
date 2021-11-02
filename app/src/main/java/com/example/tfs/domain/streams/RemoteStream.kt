package com.example.tfs.domain

import com.example.tfs.domain.topic.Reaction

data class RemoteStream(
    val streamId: Int,
    val streamName: String,
    val childTopics: List<RemoteTopic>,
)

data class RemoteTopic(
    val topicId: Int,
    val topicName: String,
    val parentStreamId: Int,
    val parentStreamName: String,
    val topic_stat: Int,
    val postList: List<Post>,
)

data class Post(
    val messageId: Int,
    val userId: Int,
    val userName: String,
    var reaction: List<Reaction> = emptyList(),
    val message: String,
    val avatar: Int? = null,
    val timeStamp: Long,
)

fun RemoteTopic.toDomainTopic() = StreamItemList.TopicItem(topicId, topicName, parentStreamId, parentStreamName, topic_stat)

fun RemoteStream.toDomainStream() = StreamItemList.StreamItem(streamId, streamName, childTopics.map { it.toDomainTopic()})

fun Post.toDomainOwnerPost() = TopicItem.OwnerPostItem(reaction = reaction, message = message, timeStamp = timeStamp)

fun Post.toDomainUserPost() = TopicItem.UserPostItem(messageId, userId, userName, reaction, message, avatar, timeStamp)
