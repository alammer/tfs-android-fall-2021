package com.example.tfs.ui.topic.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.topic.UiItemReaction
import com.example.tfs.ui.topic.customview.EmojisLayout
import com.example.tfs.ui.topic.customview.addReaction

class OwnerPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val postMessage = itemView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = itemView.findViewById<EmojisLayout>(R.id.lEmojis)

    fun setMessageText(message: CharSequence) {
        postMessage.text = message
    }

    fun setPostTapListener(postId: Int, postClick: (postId: Int, isOwner: Boolean) -> Unit) {
        itemView.setOnLongClickListener {
            postClick(postId, true)
            return@setOnLongClickListener true
        }
    }

    fun createPostReaction(reaction: List<UiItemReaction>) {
        emojiGroup.removeAllViews()
        emojiGroup.addReaction(reaction, true)
    }
}