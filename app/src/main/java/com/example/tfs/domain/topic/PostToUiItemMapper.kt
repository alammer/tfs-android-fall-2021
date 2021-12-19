package com.example.tfs.domain.topic

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.text.HtmlCompat
import com.example.tfs.R
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
        reaction = createUiReactionList(reaction)
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
        content = mapPostContextToView(post.senderName, post.content,post.timeStamp)
    )

private fun mapPostContextToView(
    userName: String,
    message: String,
    timeStamp: Long,
    ): String {
    val userNameLength = userName.length
    val spannedMessage = trimSpannable(getHtml(message))
    val messageLength = spannedMessage.length
    val spannedTimeStamp = timeStamp.postTimeStamp
    val timeStampLength = spannedTimeStamp.length
    val spannablePost = SpannableString("$userName\n$spannedMessage\n$spannedTimeStamp")

    spannablePost.setSpan(
        AbsoluteSizeSpan(USERNAME_TEXT_SIZE_SP.spToPx.toInt()),
        0,
        userNameLength,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

/*    spannablePost.setSpan(
        ForegroundColorSpan(Color.parseColor(USERNAME_TEXT_COLOR)),
        0,
        userNameLength,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )*/
    spannablePost.setSpan(
        AbsoluteSizeSpan(MESSAGE_TEXT_SIZE_SP.spToPx.toInt()),
        userNameLength + 1,
        userNameLength + 1 + messageLength,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    return spannablePost.toString()
}

private fun getHtml(htmlBody: String): Spanned{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(htmlBody, Html.FROM_HTML_MODE_COMPACT)
    else
        Html.fromHtml(htmlBody)
}

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

private fun getPostDescription(comment: String) =
    SpannableStringBuilder(comment).apply {
/*        setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            nickName.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )*/
    }

private fun createUiReactionList(
    reaction: List<LocalReaction>,
): List<UiItemReaction> {


    if (reaction.isNullOrEmpty()) {
        return emptyList()
    }

    val domainReaction = reaction
        .associate { item -> item.name to reaction.count { it.name == item.name } }
        .toList()
        .map { (name, count) ->
            DomainReaction(
                emojiName = name,
                emojiCode = reaction.first { it.name == name }.run {
                    if (isCustom) name else code
                },
                unicodeGlyph = reaction.first { it.name == name }.run {
                    if (isCustom) "ZCE" else code.getUnicodeGlyph()
                },
                count = count,
            )
        }

    val itemReaction = mutableMapOf<String, UiItemReaction>()

    domainReaction.forEach { emoji ->
        if (itemReaction.keys.contains(emoji.emojiCode)) {
            itemReaction[emoji.emojiCode]?.apply {
                val newCount = emoji.count + count
                itemReaction[emoji.emojiCode] =
                    UiItemReaction(emojiName, emojiCode, unicodeGlyph, newCount, isClicked)
            }
        } else {
            itemReaction[emoji.emojiCode] =
                UiItemReaction(
                    emoji.emojiName,
                    emoji.emojiCode,
                    emoji.unicodeGlyph,
                    emoji.count,
                    checkEmoji(reaction, emoji.emojiCode)
                )
        }
    }

    return itemReaction.toList().map { it.second }.sortedByDescending { it.count }
}

private fun checkEmoji(reaction: List<LocalReaction>, emojiCode: String): Boolean =
    reaction.asSequence().filter { it.code == emojiCode }
        .firstOrNull { it.isClicked }?.isClicked ?: false

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
private const val POST_TIME_TEXT_SIZE_SP = 12
private const val USERNAME_TEXT_COLOR = R.color.green_bg.toString()