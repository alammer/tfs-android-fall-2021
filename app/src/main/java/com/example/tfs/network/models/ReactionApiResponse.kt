package com.example.tfs.network.models

import com.example.tfs.domain.topic.DomainReaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class PostReaction(
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("reaction_type")
    val type: String,
    @SerialName("user_id")
    val userId: Int,
)

//fun PostReaction.toDomainReaction() =
//    DomainReaction(emojiName = emojiName, userId = userId)



