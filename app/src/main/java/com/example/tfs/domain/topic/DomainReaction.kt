package com.example.tfs.domain.topic

import kotlin.random.Random

data class DomainReaction(
    val emojiName: String,
    val emojiCode: String,
    val emojiGliph: String,
    val count: Int,
    val isClicked: Boolean = Random.nextBoolean()
)

