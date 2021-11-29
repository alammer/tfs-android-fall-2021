package com.example.tfs.domain.topic

import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.ui.topic.getUnicodeGlyph
import com.example.tfs.util.fullDate
import com.example.tfs.util.shortDate
import com.example.tfs.util.startOfDay
import com.example.tfs.util.year
import java.util.*

internal class TopicToItemMapper : (List<PostWithReaction>) -> UiTopicListObject {

    override fun invoke(postList: List<PostWithReaction>): UiTopicListObject =
        createDomainPostItemList(postList)

    private fun createDomainPostItemList(
        rawList: List<PostWithReaction>,
    ): UiTopicListObject {

        val datedPostList = mutableListOf<PostItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis() + localOffset

        val upAnchorId = rawList.firstOrNull()?.post?.postId ?: 0
        val downAnchorId = rawList.lastOrNull()?.post?.postId ?: 0
        val localLength = rawList.size

        rawList.forEach { post ->
            if (post.post.timeStamp.startOfDay(localOffset / 1000L) > startTopicDate) {
                startTopicDate = post.post.timeStamp.startOfDay(localOffset / 1000L)
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(PostItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(PostItem.LocalDateItem(startTopicDate.shortDate))
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
    PostItem.OwnerPostItem(id = post.postId,
        message = post.content,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction))

fun PostWithReaction.toUserPostItem() =
    PostItem.UserPostItem(id = post.postId,
        userId = post.senderId,
        userName = post.senderName,
        message = post.content,
        avatar = post.avatarUrl,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction))


private fun createUiReactionList(
    reaction: List<LocalReaction>,
): List<UiItemReaction> {


    if (reaction.isNullOrEmpty()) {
        return emptyList()
    }

    val domainReaction = reaction
        .associate { item -> (item.name to item.isClicked) to reaction.count { it.name == item.name } }
        .toList()
        .map { (emoji, count) ->
            DomainReaction(
                emojiName = emoji.first,
                emojiCode = reaction.first { it.name == emoji.first }.run {
                    if (isCustom) name else code
                },
                unicodeGlyph = reaction.first { it.name == emoji.first }.run {
                    if (isCustom) "ZCE" else code.getUnicodeGlyph()
                },
                count = count,
                isClicked = emoji.second)
        }

    val itemReaction = mutableMapOf<String, UiItemReaction>()

    domainReaction.forEach { emoji ->
        if (itemReaction.keys.contains(emoji.emojiCode)) {
            itemReaction[emoji.emojiCode]?.apply {
                val newCount = emoji.count + count
                val newClicked =
                    if (emoji.isClicked) true else isClicked
                itemReaction[emojiCode] =
                    UiItemReaction(emojiName, emojiCode, unicodeGlyph, newCount, newClicked)
            }
        } else {
            itemReaction[emoji.emojiCode] =
                UiItemReaction(emoji.emojiName, emoji.emojiCode, emoji.unicodeGlyph, emoji.count, emoji.isClicked)
        }
    }

    return itemReaction.toList().map { it.second }.sortedByDescending { it.count }
}

data class UiItemReaction(
    val emojiName: String,
    val emojiCode: String,
    val unicodeGlyph: String,
    val count: Int,
    val isClicked: Boolean = false,
)

data class UiTopicListObject(
    val itemList: List<PostItem>,
    val upAnchorId: Int,
    val downAnchorId: Int,
    val localDataLength: Int,
)

private val localOffset =
    (TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings).toLong()