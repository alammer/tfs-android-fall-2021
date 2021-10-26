package com.example.tfs.data

sealed class TopicCell {

    class LocalDateCell(val postDate: String) : TopicCell()
    class PostCell(
        var reaction: MutableList<Reaction> = mutableListOf(),
        val message: String,
        val isOwner: Boolean = false,
        var avatar: Int? = null,
        var timeStamp: Long,
    ) : TopicCell()
}

class Reaction(
    val emoji: Int,
    var count: Int,
    val userId: List<Int>? = null,
    var isClicked: Boolean = false
)
