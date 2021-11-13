package com.example.tfs.network.models

import com.example.tfs.domain.topic.DomainReaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("user")
    val userInfo: User
)

@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
)

fun PostReaction.toDomainReaction() =
    DomainReaction(emojiCode = emojiCode, emojiName = emojiName, userId = userId, username = userInfo.name)



