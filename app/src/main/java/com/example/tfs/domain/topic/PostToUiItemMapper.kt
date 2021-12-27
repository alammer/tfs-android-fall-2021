package com.example.tfs.domain.topic

import android.graphics.Color
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.ui.topic.getUnicodeGlyph
import com.example.tfs.util.*
import java.util.*

internal class TopicToUiItemMapper : (List<PostWithReaction>) -> UiTopicListObject {

    override fun invoke(postList: List<PostWithReaction>): UiTopicListObject =
        createDomainPostItemList(postList)

    private fun createDomainPostItemList(
        rawList: List<PostWithReaction>,
    ): UiTopicListObject {

        if (rawList.isEmpty()) return UiTopicListObject()

        val datedPostList = mutableListOf<AdapterItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis() + localOffset

        val upAnchorId = rawList.firstOrNull()?.post?.postId ?: 0
        val downAnchorId = rawList.lastOrNull()?.post?.postId ?: 0
        val localLength = rawList.size

        rawList.forEach { post ->
            if (post.post.timeStamp.startOfDay(localOffset / 1000L) > startTopicDate) {
                startTopicDate = post.post.timeStamp.startOfDay(localOffset / 1000L)
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(DomainPostDate(startTopicDate.fullDate))
                } else {
                    datedPostList.add(DomainPostDate(startTopicDate.shortDate))
                }
            }
            if (post.post.isSelf) {
                datedPostList.add(post.toOwnerPostItem())
            } else {
                datedPostList.add(post.toUserPostItem())
            }
        }

        return UiTopicListObject(
            itemList = datedPostList.toList(),
            upAnchorId = upAnchorId,
            downAnchorId = downAnchorId,
            localDataLength = localLength,
        )
    }
}

fun PostWithReaction.toOwnerPostItem() =
    DomainOwnerPost(
        id = post.postId,
        message = post.content,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction),
        content = spanOwnerPost(post.content, post.timeStamp)
    )

fun PostWithReaction.toUserPostItem() =
    DomainUserPost(
        id = post.postId,
        userId = post.senderId,
        userName = post.senderName,
        message = post.content,
        avatar = post.avatarUrl,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction),
        content = spanUserPost(post.senderName, post.content, post.timeStamp)
    )

private fun spanOwnerPost(
    message: String,
    timeStamp: Long,
): SpannableString {
    val messageLength = message.length
    val spannedTimeStamp = timeStamp.postTimeStamp
    val timeStampStart = messageLength + 1
    val spannablePost = SpannableString("$message\n$spannedTimeStamp")

    spannablePost.setSpan(
        AbsoluteSizeSpan(TIMESTAMP_TEXT_SIZE_SP.spToPx.toInt()),
        timeStampStart,
        spannablePost.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    spannablePost.setSpan(
        ForegroundColorSpan(Color.LTGRAY),
        timeStampStart,
        spannablePost.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    spannablePost.setSpan(
        AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
        timeStampStart,
        spannablePost.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    return spannablePost
}

private fun spanUserPost(
    userName: String,
    message: String,
    timeStamp: Long,
): SpannableString {
    val userNameLength = userName.length
    val messageLength = message.length
    val spannedTimeStamp = timeStamp.postTimeStamp
    val timeStampStart = userNameLength + messageLength + 2
    val spannablePost = SpannableString("$userName\n$message\n$spannedTimeStamp")

    spannablePost.setSpan(
        AbsoluteSizeSpan(USERNAME_TEXT_SIZE_SP.spToPx.toInt()),
        0,
        userNameLength,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    spannablePost.setSpan(
        ForegroundColorSpan(Color.parseColor(USER_TEXT_COLOR)),
        0,
        userNameLength,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    spannablePost.setSpan(
        AbsoluteSizeSpan(MESSAGE_TEXT_SIZE_SP.spToPx.toInt()),
        userNameLength + 1,
        timeStampStart - 2,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    spannablePost.setSpan(
        AbsoluteSizeSpan(TIMESTAMP_TEXT_SIZE_SP.spToPx.toInt()),
        timeStampStart,
        spannablePost.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    spannablePost.setSpan(
        ForegroundColorSpan(Color.parseColor(USER_TEXT_COLOR)),
        timeStampStart,
        spannablePost.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    spannablePost.setSpan(
        AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
        timeStampStart,
        spannablePost.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )
    return spannablePost
}

private fun createUiReactionList(
    reaction: List<LocalReaction>,
): List<UiItemReaction> {


    if (reaction.isNullOrEmpty()) {
        return emptyList()
    }

    val domainReaction = reaction
        .associate { item -> item.code to reaction.count { it.code == item.code } }
        .toList()
        .map { (code, count) ->
            UiItemReaction(
                emojiName = reaction.first { it.code == code }.name,
                emojiCode = reaction.first { it.code == code }.run {
                    if (isCustom) name else code
                },
                unicodeGlyph = reaction.first { it.code == code }.run {
                    if (isCustom) "ZCE" else code.getUnicodeGlyph()
                },
                count = count,
                isClicked = reaction.filter { it.code == code }.any { it.isClicked }
            )
        }

    return domainReaction.sortedByDescending { it.count }
}

data class UiItemReaction(
    val emojiName: String,
    val emojiCode: String,
    val unicodeGlyph: String,
    val count: Int,
    val isClicked: Boolean,
)

data class UiTopicListObject(
    val itemList: List<AdapterItem> = emptyList(),
    val upAnchorId: Int = -1,
    val downAnchorId: Int = -1,
    val localDataLength: Int = 0,
)

private val localOffset =
    (TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings).toLong()

private const val USERNAME_TEXT_SIZE_SP = 14
private const val MESSAGE_TEXT_SIZE_SP = 16
private const val TIMESTAMP_TEXT_SIZE_SP = 12
private const val USER_TEXT_COLOR = "#2A9D8F"