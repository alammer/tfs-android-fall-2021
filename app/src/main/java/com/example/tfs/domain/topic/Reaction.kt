package com.example.tfs.domain.topic

data class Reaction(
    val emoji: Int,
    val count: Int,
    val userId: List<Int> = emptyList(),
)
