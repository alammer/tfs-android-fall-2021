package com.example.tfs.presentation.streams

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class TopicItemViewHolder(topicView: View) : RecyclerView.ViewHolder(topicView) {

    val topicName: TextView = topicView.findViewById<TextView>(R.id.tvTopicName)
    val topicStat: TextView = topicView.findViewById<TextView>(R.id.tvTopicStat)
}