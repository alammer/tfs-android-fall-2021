package com.example.tfs.ui.topic.adapter.items

import android.text.Spannable
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
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
    private val spanFactory: Spannable.Factory
) : BaseViewHolder<View, DomainUserPost>(postView) {

    private val userAvatar = postView.findViewById<ShapeableImageView>(R.id.imgPostAvatar)
    private val postMessage = postView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = postView.findViewById<EmojisLayout>(R.id.lEmojis)

    override fun onBind(item: DomainUserPost) {
        super.onBind(item)

        postMessage.setSpannableFactory(spanFactory)

        postMessage.setText(item.content, TextView.BufferType.SPANNABLE)

        userAvatar.apply {
            item.avatar?.let {
                loadAvatar(it)
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

    private fun loadAvatar(avatarUrl: String) {
        Glide.with(postView)
            .load(avatarUrl)
            .centerCrop()
            .placeholder(R.drawable.loading_img_animation)
            .error(R.drawable.broken_img)
            .into(userAvatar)
            .waitForLayout()
    }
}