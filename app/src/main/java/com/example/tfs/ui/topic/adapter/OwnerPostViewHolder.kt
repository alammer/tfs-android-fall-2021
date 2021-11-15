package com.example.tfs.ui.topic.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.ui.topic.customview.EmojisLayout
import com.example.tfs.ui.topic.customview.addReaction

class OwnerPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textMessage = itemView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = itemView.findViewById<EmojisLayout>(R.id.lEmojis)

    fun setMessageText(message: CharSequence) {
        textMessage.text = message
    }

    fun createPostReaction(reaction: List<ItemReaction>) {
        emojiGroup.removeAllViews()
        emojiGroup.addReaction(reaction, true)
    }
}