package com.example.tfs.presentation.topic

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.customviews.EmojisLayout

class OwnerPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textMessage = itemView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = itemView.findViewById<EmojisLayout>(R.id.lEmojis)

    fun setMessageText(message: String) {
        textMessage.text = message
    }

    fun getEmojilayout(): EmojisLayout = emojiGroup
}