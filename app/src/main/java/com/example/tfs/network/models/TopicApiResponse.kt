package com.example.tfs.network.models

import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.HtmlCompat
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

fun RemotePost.toLocalPostWithReaction(ownerId: Int) =
    PostWithReaction(
        LocalPost(
            postId = id,
            topicName = topicName,
            streamName = streamName,
            senderId = senderId,
            isSelf = senderId == ownerId,
            senderName = senderName,
            content = trimSpannable(getHtml(content)).toString(),
            avatarUrl = avatar,
            timeStamp = timeStamp,
            postFlags = postStatus,
        ),
        reaction = reaction.map { it.toLocalReaction(id, ownerId) }
    )

fun PostReaction.toLocalReaction(postId: Int, ownerId: Int) =
    LocalReaction(
        postId = postId,
        name = name,
        code = code,
        userId = userId,
        isClicked = userId == ownerId,
        isCustom = type == "zulip_extra_emoji",
    )

private fun getHtml(htmlBody: String): Spanned =
    HtmlCompat.fromHtml(htmlBody, HtmlCompat.FROM_HTML_MODE_COMPACT)

private fun trimSpannable(spanned: Spanned): SpannableStringBuilder {
    val spannable = SpannableStringBuilder(spanned)
    var trimStart = 0
    var trimEnd = 0
    var text = spannable.toString()
    while (text.isNotEmpty() && text.startsWith("\n")) {
        text = text.substring(1)
        trimStart += 1
    }
    while (text.isNotEmpty() && text.endsWith("\n")) {
        text = text.substring(0, text.length - 1)
        trimEnd += 1
    }
    return spannable.delete(0, trimStart).delete(spannable.length - trimEnd, spannable.length)
}




