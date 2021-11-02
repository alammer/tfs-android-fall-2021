package com.example.tfs.presentation.streams

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.StreamItemList

class StreamItemViewHolder(streamView: View) : RecyclerView.ViewHolder(streamView) {

    private val streamName: TextView = streamView.findViewById<TextView>(R.id.tvStreamName)
    private val btnTopicList: ImageView = streamView.findViewById<ImageView>(R.id.btnShowTopic)

    fun setStreamName(name: String) {
        streamName.text = name
    }

    fun setStreamExpandIcon(icon: Int) {
        btnTopicList.setImageResource(icon)
    }

    fun setStreamExpandClickListener(changeExpandState: (StreamItemList.StreamItem) -> Unit, item: StreamItemList.StreamItem) {
        btnTopicList.setOnClickListener { changeExpandState(item) }
    }
}