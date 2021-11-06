package com.example.tfs.domain.topic

data class Reaction(
    val emojiCode: String,
    val emojiName: String,
    val owner: Int,
    val count: Int,
)
