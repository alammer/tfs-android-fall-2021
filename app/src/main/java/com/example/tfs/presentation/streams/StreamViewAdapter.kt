package com.example.tfs.presentation.streams

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.StreamCell
import com.example.tfs.presentation.topic.DateViewHolder
import com.example.tfs.presentation.topic.PostViewHolder
import com.example.tfs.presentation.topic.TopicAdapterCallback

class StreamViewAdapter(private val clickListener: ItemClickListener):
    ListAdapter<StreamCell, RecyclerView.ViewHolder>(StreamDiffCallback()) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is StreamCell.StreamNameCell -> R.layout.item_stream_rv_header
        is StreamCell.TopicNameCell -> R.layout.item_stream_rv_topic
        null -> throw IllegalStateException("Unknown view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)
        Log.i("StreamViewAdapter", "Function called: 0")
        return when (viewType) {
            R.layout.item_stream_rv_header -> StreamViewHolder(v)
            R.layout.item_stream_rv_topic -> TopicViewHolder(v)
            else -> {
                Log.i("StreamViewAdapter", "Function called: -1")
                throw IllegalStateException("Unknown viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StreamViewHolder -> {
                Log.i("StreamViewAdapter", "Function called: 0")
                val item = getItem(position) as StreamCell.StreamNameCell
                holder.streamName.text = item.streamName
                holder.btnTopicList.setOnClickListener { clickListener.onClick(item) }

            }
            is TopicViewHolder -> {
                Log.i("StreamViewAdapter", "Function called: 2")
                val item = getItem(position) as StreamCell.TopicNameCell
                holder.topicName.text = item.topicName
                holder.topicStat.text = item.messageStat.toString()
                holder.itemView.setOnClickListener { clickListener.onClick(item) }
            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }

    }
}

private class StreamDiffCallback : DiffUtil.ItemCallback<StreamCell>() {

    override fun areItemsTheSame(oldItem: StreamCell, newItem: StreamCell): Boolean {

        val isSameStreamItem = oldItem is StreamCell.StreamNameCell
                && newItem is StreamCell.StreamNameCell
                && oldItem.streamName == newItem.streamName

        val isSameTopicItem = oldItem is StreamCell.TopicNameCell
                && newItem is StreamCell.TopicNameCell
                && oldItem.topicName== newItem.topicName

        return isSameStreamItem || isSameTopicItem
    }

    override fun areContentsTheSame(oldItem: StreamCell, newItem: StreamCell) = oldItem == newItem
}

class ItemClickListener(val clickListener: (item: StreamCell) -> Unit) {
    fun onClick(item: StreamCell) = clickListener(item)
}