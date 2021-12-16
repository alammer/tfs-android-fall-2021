package com.example.tfs.ui.topic.adapter.items

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.tfs.R
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.topic.DomainUserPost
import com.example.tfs.domain.topic.UiItemReaction
import com.example.tfs.ui.topic.customview.EmojisLayout
import com.example.tfs.ui.topic.customview.addReaction
import com.example.tfs.util.drawUserInitials
import com.google.android.material.imageview.ShapeableImageView

class UserPostItemViewHolder(
    private val postView: View,
    private val onChangeReactionClick: (postId: Int, emojiName: String, emojiCode: String) -> Unit,
    private val onAddReactionClick: (postId: Int) -> Unit,
    private val onPostTap: (postId: Int, isOwner: Boolean) -> Unit,
) : BaseViewHolder<View, DomainUserPost>(postView) {

    private val userAvatar = postView.findViewById<ShapeableImageView>(R.id.imgPostAvatar)
    private val userName = postView.findViewById<TextView>(R.id.tvPostUserName)
    private val postMessage = postView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = postView.findViewById<EmojisLayout>(R.id.lEmojis)

    override fun onBind(item: DomainUserPost) {
        super.onBind(item)

        Log.e("UserPostItemViewHolder", "Function called: onBind() $item")

        userName.text = item.userName

        postMessage.text = item.message

        userAvatar.apply {
            item.avatar?.let {
                loadAvatar()
            } ?: drawUserInitials(item.userName)
        }

        postView.setOnLongClickListener {
            onPostTap(item.id, false)
            return@setOnLongClickListener true
        }

        createPostReaction(item.reaction)
    }

    private fun createPostReaction(reaction: List<UiItemReaction>) {
        emojiGroup.removeAllViews()
        if (reaction.isNotEmpty()) {
            emojiGroup.addReaction(reaction)
            addReactionListeners(item.id, reaction, onChangeReactionClick, onAddReactionClick)
        }
    }

    private fun addReactionListeners(
        postId: Int,
        reaction: List<UiItemReaction>,
        onEmojiClick: (postId: Int, emojiName: String, emojiCode: String) -> Unit,
        onAddReactionClick: (postId: Int) -> Unit,
    ) {
        (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
            emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                onEmojiClick(
                    postId,
                    reaction[emojiPosition].emojiName,
                    reaction[emojiPosition].emojiCode
                )
            }
        }
        //click on "+"
        emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
            onAddReactionClick(postId)
        }
    }

    private fun loadAvatar() = Unit

}