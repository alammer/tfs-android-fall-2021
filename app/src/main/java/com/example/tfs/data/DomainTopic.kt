package com.example.tfs.data

sealed class TopicItem {

    class LocalDateItem(val postDate: String) : TopicItem()
    data class UserPostItem(
        val userId: Int,
        val userName: String,
        var reaction: List<Reaction>? = null,
        val message: String,
        var avatar: Int? = null,
        var timeStamp: Long,
    ) : TopicItem()

    data class OwnerPostItem(
        var reaction: List<Reaction>? = null,
        val message: String,
        var timeStamp: Long,
    ) : TopicItem()
}

