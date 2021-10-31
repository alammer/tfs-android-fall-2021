package com.example.tfs.data

data class Reaction(
    val emoji: Int,
    var count: Int,
    val userId: List<Int>? = null,
    var isClicked: Boolean = false
)
