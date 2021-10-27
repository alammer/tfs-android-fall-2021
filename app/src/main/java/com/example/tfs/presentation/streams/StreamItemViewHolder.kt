package com.example.tfs.presentation.streams

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class StreamItemViewHolder(streamView: View) : RecyclerView.ViewHolder(streamView) {

    val streamName: TextView = streamView.findViewById<TextView>(R.id.tvStreamName)
    val btnTopicList: ImageView = streamView.findViewById<ImageView>(R.id.btnShowTopic)
}