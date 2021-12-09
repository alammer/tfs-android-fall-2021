package com.example.tfs.ui.stream.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.streams.StreamListItem

class TopicItemViewHolder(private val topicView: View) : RecyclerView.ViewHolder(topicView) {

    private val topicName: TextView = topicView.findViewById<TextView>(R.id.tvTopicName)
    private val topicStat: TextView = topicView.findViewById<TextView>(R.id.tvTopicStat)

    fun setTopicName(name: String) {
        topicName.text = name
    }

    fun setTopicStat(messageCount: Int) {
        topicStat.text = messageCount.toString()
    }

    fun setTopicActivityColor(colorIndicator: Int) {
        topicView.setBackgroundColor(colorIndicator)
    }

    fun setTopicClickListener(
        item: StreamListItem.TopicItem,
        selectTopic: (StreamListItem) -> Unit
    ) {
        topicView.setOnClickListener { selectTopic(item) }
    }
}