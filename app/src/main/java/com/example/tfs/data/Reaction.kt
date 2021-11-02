package com.example.tfs.data

data class Reaction(
    val emoji: Int,
    val count: Int,
    val userId: List<Int> = emptyList(),
    val isClicked: Boolean = false
)
