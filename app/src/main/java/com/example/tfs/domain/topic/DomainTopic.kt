package com.example.tfs.domain.topic

sealed class TopicItem {

    data class LocalDateItem(val postDate: String) : TopicItem()

    data class UserPostItem(
        val id: Int,
        val userId: Int,
        val userName: String,
        val reaction: List<Reaction> = emptyList(),
        val message: String,
        val avatar: String? = null,
        val timeStamp: Long,
    ) : TopicItem()

    data class OwnerPostItem(
        val id: Int,
        val reaction: List<Reaction> = emptyList(),
        val message: String,
        val timeStamp: Long,
    ) : TopicItem()
}

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

