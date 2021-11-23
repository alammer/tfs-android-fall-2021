package com.example.tfs.network.models

import com.example.tfs.database.entity.LocalPost
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostListResponse(
    @SerialName("messages")
    val remotePostList: List<RemotePost> = emptyList(),
)

@Serializable
data class RemotePost(
    @SerialName("id")
    val id: Int,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("sender_full_name")
    val senderName: String,
    @SerialName("display_recipient")
    val streamName: String,
    @SerialName("subject")
    val topicName: String,
    @SerialName("content")
    val content: String,
    @SerialName("content_type")
    val content_type: String,
    @SerialName("avatar_url")
    val avatar: String? = null,
    @SerialName("timestamp")
    val timeStamp: Long,
    @SerialName("flags")
    val postStatus: List<String>,
    @SerialName("reactions")
    var reaction: List<PostReaction> = emptyList(),
)

@Serializable
data class PostReaction(
    @SerialName("emoji_name")
    val name: String,
    @SerialName("emoji_code")
    val code: String,
    @SerialName("reaction_type")
    val type: String,
    @SerialName("user_id")
    val userId: Int,
)

fun RemotePost.toLocalPostWithReaction() =
    PostWithReaction(
        LocalPost(
            postId = id,
            topicName = topicName,
            streamName = streamName,
            senderId = senderId,
            senderName = senderName,
            content = content,
            avatarUrl = avatar,
            timeStamp = timeStamp,
            postFlags = postStatus,
        ),
        reaction = reaction.map { it.toLocalReaction(id) }
    )

fun PostReaction.toLocalReaction(postId: Int) =
    LocalReaction(
        postId = postId,
        name = name,
        code = code,
        userId = userId,
        isCustom = type == "zulip_extra_emoji",
    )




