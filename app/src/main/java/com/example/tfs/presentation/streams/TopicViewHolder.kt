package com.example.tfs.presentation.streams

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class TopicViewHolder(topicView: View) : RecyclerView.ViewHolder(topicView) {

    val topicName = topicView.findViewById<TextView>(R.id.tvTopicName)
    val topicStat = topicView.findViewById<TextView>(R.id.tvTopicStat)
}