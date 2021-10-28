package com.example.tfs.data

sealed class TopicItem {

    class LocalDateItem(val postDate: String) : TopicItem()
    class PostItem(
        var reaction: MutableList<Reaction> = mutableListOf(),
        val message: String,
        val isOwner: Boolean = false,
        var avatar: Int? = null,
        var timeStamp: Long,
    ) : TopicItem()
}

class Reaction(
    val emoji: Int,
    var count: Int,
    val userId: List<Int>? = null,
    var isClicked: Boolean = false
)
