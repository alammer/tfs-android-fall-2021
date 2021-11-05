package com.example.tfs.domain.topic

data class Reaction(
    val emoji: Int,
    val count: Int,
    val userList: List<Int> = emptyList(),
)
