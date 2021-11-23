package com.example.tfs.ui.topic.adapter

import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.domain.topic.DomainReaction
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.ui.topic.getUnicodeGlyph
import com.example.tfs.util.fullDate
import com.example.tfs.util.shortDate
import com.example.tfs.util.startOfDay
import com.example.tfs.util.year
import java.util.*

internal class TopicToItemMapper : (List<PostWithReaction>, Int) -> TopicListObject {

    override fun invoke(postList: List<PostWithReaction>, ownerId: Int): TopicListObject =
        createDomainPostItemList(postList, ownerId)

    private fun createDomainPostItemList(
        rawList: List<PostWithReaction>,
        ownerId: Int,
    ): TopicListObject {

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
            if (post.post.senderId == ownerId) {
                datedPostList.add(post.toOwnerPostItem())
            } else {
                datedPostList.add(post.toUserPostItem(ownerId))
            }
        }

        return TopicListObject(
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

fun PostWithReaction.toUserPostItem(ownerId: Int) =
    PostItem.UserPostItem(id = post.postId,
        userId = post.senderId,
        userName = post.senderName,
        message = post.content,
        avatar = post.avatarUrl,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction, ownerId))


private fun createUiReactionList(
    reaction: List<LocalReaction>,
    ownerId: Int = -1,
): List<ItemReaction> {


    if (reaction.isNullOrEmpty()) {
        return emptyList()
    }

    val domainReaction = reaction
        .associate { emoji -> emoji.name to reaction.count { it.name == emoji.name } }
        .toList()
        .map { (name, count) ->
            DomainReaction(
                emojiName = name,
                emojiCode = reaction.first { it.name == name }.run {
                    if (isCustom) name else code
                },
                emojiGlyph = reaction.first { it.name == name }.run {
                    if (isCustom) "ZCE" else code.getUnicodeGlyph()
                },
                count = count,
                isClicked = checkEmoji(reaction, name, ownerId))
        }

    val itemReaction = mutableMapOf<String, ItemReaction>()

    domainReaction.forEach { emoji ->
        if (itemReaction.keys.contains(emoji.emojiCode)) {
            itemReaction[emoji.emojiCode]?.apply {
                val newCount = emoji.count + count
                val newClicked =
                    if (emoji.isClicked) true else isClicked
                itemReaction[emoji.emojiCode] =
                    ItemReaction(emoji.emojiCode, emoji.emojiGlyph, newCount, newClicked)
            }
        } else {
            itemReaction[emoji.emojiCode] =
                ItemReaction(emoji.emojiCode, emoji.emojiGlyph, emoji.count, emoji.isClicked)
        }
    }

    return itemReaction.toList().map { it.second }.sortedByDescending { it.count }
}

private fun checkEmoji(reaction: List<LocalReaction>, emojiName: String, ownerId: Int): Boolean {
    reaction.asSequence().filter { it.code == emojiName }
        .firstOrNull { it.userId == ownerId }
        ?.let { return true } ?: return false
}

data class ItemReaction(
    val emojiCode: String,
    val unicodeGlyph: String,
    val count: Int,
    val isClicked: Boolean = false,
)

data class TopicListObject(
    val itemList: List<PostItem>,
    val upAnchorId: Int,
    val downAnchorId: Int,
    val localDataLength: Int,
)

private val localOffset =
    (TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings).toLong()