package com.example.tfs.domain.topic

data class DomainReaction(
    val emojiName: String,
    val emojiCode: String,
    val emojiGlyph: String,
    val count: Int,
    val isClicked: Boolean,
)

