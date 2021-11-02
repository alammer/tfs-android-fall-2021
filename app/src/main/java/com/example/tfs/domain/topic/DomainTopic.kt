package com.example.tfs.domain

import com.example.tfs.domain.topic.Reaction

sealed class TopicItem {

    data class LocalDateItem(val postDate: String) : TopicItem()
    data class UserPostItem(
        val messageId: Int,
        val userId: Int,
        val userName: String,
        val reaction: List<Reaction> = emptyList(),
        val message: String,
        val avatar: Int? = null,
        val timeStamp: Long,
    ) : TopicItem()

    data class OwnerPostItem(
        val reaction: List<Reaction> = emptyList(),
        val message: String,
        val timeStamp: Long,
    ) : TopicItem()
}

