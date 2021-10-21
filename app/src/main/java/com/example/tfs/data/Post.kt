package com.example.tfs.data

import java.time.LocalDateTime

sealed class TopicCell {
    class DateCell(val timeStamp: LocalDateTime) : TopicCell()
    class PostCell(
        var reaction: MutableList<Reaction> = mutableListOf(),
        val message: String,
        val isOwner: Boolean = false,
        var avatar: Int? = null,
        var timeStamp: LocalDateTime
    ) : TopicCell()
}


class Reaction(
    val emoji: Int,
    var count: Int,
    val userId: List<Int>? = null,
    var isClicked: Boolean = false
)