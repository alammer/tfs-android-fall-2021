package com.example.tfs.ui.stream.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.streams.StreamListItem

class StreamItemViewHolder(streamView: View) : RecyclerView.ViewHolder(streamView) {

    private val streamName: TextView = streamView.findViewById<TextView>(R.id.tvStreamName)
    private val btnTopicList: ImageView = streamView.findViewById<ImageView>(R.id.btnShowTopic)

    fun setStreamName(name: String) {
        streamName.text = name
    }

    fun setStreamExpandIcon(icon: Int) {
        btnTopicList.setImageResource(icon)
    }

    fun setStreamExpandClickListener(
        changeExpandState: (StreamListItem.StreamItem) -> Unit,
        item: StreamListItem.StreamItem,
    ) {
        btnTopicList.setOnClickListener { changeExpandState(item) }
    }
}