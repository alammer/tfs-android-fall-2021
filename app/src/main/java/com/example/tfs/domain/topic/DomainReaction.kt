package com.example.tfs.domain.topic

import kotlin.random.Random

data class DomainReaction(
    val emojiCode: String,
    val emojiName: String,
    val userId: Int,
    val username: String,
    val isClicked: Boolean = Random.nextBoolean()
)

