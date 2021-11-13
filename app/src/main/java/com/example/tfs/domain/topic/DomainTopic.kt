package com.example.tfs.domain.topic

sealed class PostItem {

    data class LocalDateItem(val postDate: String) : PostItem()

    data class UserPostItem(
        val id: Int,
        val userId: Int,
        val userName: String,
        val reaction: List<DomainReaction> = emptyList(),
        val message: String,
        val avatar: String? = null,
        val timeStamp: Long,
    ) : PostItem()

    data class OwnerPostItem(
        val id: Int,
        val reaction: List<DomainReaction> = emptyList(),
        val message: String,
        val timeStamp: Long,
    ) : PostItem()
}

