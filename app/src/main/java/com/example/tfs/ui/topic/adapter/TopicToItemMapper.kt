package com.example.tfs.ui.topic.adapter

import android.util.Log
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.domain.topic.DomainReaction
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.ui.topic.getUnicodeGlyph
import com.example.tfs.util.fullDate
import com.example.tfs.util.shortDate
import com.example.tfs.util.startOfDay
import com.example.tfs.util.year

internal class TopicToItemMapper : (List<PostWithReaction>) -> (List<PostItem>) {

    override fun invoke(postList: List<PostWithReaction>): List<PostItem> =
        createDomainPostItemList(postList)


    private fun createDomainPostItemList(rawList: List<PostWithReaction>): List<PostItem> {

        Log.i("TopicToItemMapper", "Function called: createDomainPostItemList() $rawList")

        val datedPostList = mutableListOf<PostItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        rawList.forEach { post ->
            if (post.post.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = post.post.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(PostItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(PostItem.LocalDateItem(startTopicDate.shortDate))
                }
            }

            /*if (Random.nextBoolean()) {
            datedPostList.add(post.toOwnerPostItem())
            } else {*/
            datedPostList.add(post.toUserPostItem())
            //}
        }

        return datedPostList.toList()
    }
}

fun PostWithReaction.toOwnerPostItem() =
    PostItem.OwnerPostItem(id = post.postId,
        message = post.content,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction, post.postId, post.senderId))

fun PostWithReaction.toUserPostItem() =
    PostItem.UserPostItem(id = post.postId,
        userId = post.senderId,
        userName = post.senderName,
        message = post.content,
        avatar = post.avatarUrl,
        timeStamp = post.timeStamp,
        reaction = createUiReactionList(reaction, post.postId, post.senderId))


private fun createUiReactionList(
    reaction: List<LocalReaction>,
    postId: Int,
    senderId: Int,
): List<ItemReaction> {

    if (reaction.isNullOrEmpty()) {
        return emptyList()
    }

    val domainReaction = reaction
        .associate { emoji -> emoji.emojiName to reaction.count { it.emojiName == emoji.emojiName } }
        .toList()
        .map { (name, count) ->
            DomainReaction(
                emojiName = name,
                emojiCode = reaction.first { it.emojiName == name }.emojiCode,
                emojiGliph = reaction.first { it.emojiName == name }.emojiCode.getUnicodeGlyph(),
                count = count,
                isClicked = checkEmoji(reaction, name))
        }

    val itemReaction = mutableMapOf<String, ItemReaction>()

    domainReaction.forEach { emoji ->
        if (itemReaction.keys.contains(emoji.emojiCode)) {
            itemReaction[emoji.emojiCode]?.apply {
                val newCount = emoji.count + count
                val newClicked =
                    if (emoji.isClicked) true else isClicked
                itemReaction[emoji.emojiCode] =
                    ItemReaction(emoji.emojiCode, emoji.emojiGliph, newCount, newClicked)
            }
        } else {
            itemReaction[emoji.emojiCode] =
                ItemReaction(emoji.emojiCode, emoji.emojiGliph, emoji.count, emoji.isClicked)
        }
    }

    return itemReaction.toList().map { it.second }.sortedByDescending { it.count }
}

private fun checkEmoji(reaction: List<LocalReaction>, name: String): Boolean {
    reaction.filter { it.emojiName == name }
        .firstOrNull { it.userId == 37 }
        ?.let { return true } ?: return false
}

data class ItemReaction(
    val emojiCode: String,
    val unicodeGliph: String,
    val count: Int,
    val isClicked: Boolean = false,
)