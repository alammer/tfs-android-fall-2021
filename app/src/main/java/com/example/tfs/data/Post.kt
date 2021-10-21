package com.example.tfs.data

class Reaction(
    val emoji: Int,
    var count: Int,
    val userId: List<Int>? = null,
    var isClicked: Boolean = false
)

class Post(
    var reaction: MutableList<Reaction> = mutableListOf(),
    val message: String,
    val isOwner: Boolean = false,
    var avatar: Int? = null
)