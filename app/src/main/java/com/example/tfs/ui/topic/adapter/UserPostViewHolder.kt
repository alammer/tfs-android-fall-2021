package com.example.tfs.ui.topic.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.topic.Reaction
import com.example.tfs.ui.topic.customview.EmojisLayout
import com.example.tfs.ui.topic.customview.addReaction
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.drawUserInitials
import com.google.android.material.imageview.ShapeableImageView


class UserPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val userAvatar = itemView.findViewById<ShapeableImageView>(R.id.imgPostAvatar)
    private val userName = itemView.findViewById<TextView>(R.id.tvPostUserName)
    private val textMessage = itemView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = itemView.findViewById<EmojisLayout>(R.id.lEmojis)

    fun setUserName(name: String) {
        userName.text = name
    }

    fun setMessageText(message: String) {
        textMessage.text = message
    }

    fun setMessageClickListener (item: Int, postClick: (Int) -> Unit) {
        textMessage.setOnLongClickListener {
            postClick(item)
            return@setOnLongClickListener true
        }
    }

    fun setUserAvatarImage(imageUri: Int) {
        userAvatar.setImageResource(imageUri)
    }

    fun setUserInitilas(userName: String) {
        userAvatar.drawUserInitials(userName, USER_AVATAR_WIDTH.dpToPixels())
    }

    fun createPostReaction(reaction: List<Reaction>) {
        emojiGroup.addReaction(reaction)
    }

    fun addReactionListeners(itemId: Int, onEmojiClick: (Int, Int) -> Unit, onAddReactionClick: (Int) -> Unit) {
        (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
            emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                onEmojiClick(itemId, it.tag as Int)
            }
        }
        //click on "+"
        emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
            onAddReactionClick(itemId)
        }
    }
}

private const val USER_AVATAR_WIDTH = 37