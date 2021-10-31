package com.example.tfs.presentation.streams

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.StreamItemList

class TopicItemViewHolder(private val topicView: View) : RecyclerView.ViewHolder(topicView) {

    private val topicName: TextView = topicView.findViewById<TextView>(R.id.tvTopicName)
    private val topicStat: TextView = topicView.findViewById<TextView>(R.id.tvTopicStat)

    fun setTopicName(name: String) {
        topicName.text = name
    }

    fun setTopicStat(messageCount: Int) {
        topicStat.text = messageCount.toString()
    }

    fun setTopicActivityColor (colorIndicator: Int) {
        topicView.setBackgroundColor(colorIndicator)
    }

    fun setTopicClickListener (selectTopic: (StreamItemList) -> Unit) {
        topicView.setOnClickListener { selectTopic }
    }
}