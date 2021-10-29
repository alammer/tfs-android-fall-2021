package com.example.tfs.presentation.streams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.StreamListItem

class StreamViewAdapter(private val onItemClicked: (StreamListItem) -> Unit) :
    ListAdapter<StreamListItem, RecyclerView.ViewHolder>(StreamDiffCallback()) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is StreamListItem.StreamItem -> R.layout.item_stream_rv_header
        is StreamListItem.TopicItem -> R.layout.item_stream_rv_topic
        null -> throw IllegalStateException("Unknown view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_stream_rv_header -> StreamItemViewHolder(v)
            R.layout.item_stream_rv_topic -> TopicItemViewHolder(v)
            else -> {
                throw IllegalStateException("Unknown viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StreamItemViewHolder -> {
                val item = getItem(position) as StreamListItem.StreamItem
                holder.streamName.text = item.streamName
                if (item.expanded) {
                    holder.btnTopicList.setImageResource(R.drawable.ic_collapce)
                } else {
                    holder.btnTopicList.setImageResource(R.drawable.ic_expand)
                }
                holder.btnTopicList.setOnClickListener { onItemClicked(item) }
            }
            is TopicItemViewHolder -> {
                val item = getItem(position) as StreamListItem.TopicItem
                holder.topicName.text = item.topicName
                holder.topicStat.text = item.messageStat.toString()
                when (item.messageStat) {
                    in 0..100 -> holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.topic_bg_color
                        )
                    )
                    else -> holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.hot_topic_bg_color
                        )
                    )
                }
                holder.itemView.setOnClickListener { onItemClicked(item) }
            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }

    }
}

private class StreamDiffCallback : DiffUtil.ItemCallback<StreamListItem>() {

    override fun areItemsTheSame(oldItem: StreamListItem, newItem: StreamListItem): Boolean {
        return (oldItem as? StreamListItem.StreamItem)?.streamId == (newItem as? StreamListItem.StreamItem)?.streamId
                || (oldItem as? StreamListItem.TopicItem)?.topicId == (newItem as? StreamListItem.TopicItem)?.topicId
    }

    override fun areContentsTheSame(oldItem: StreamListItem, newItem: StreamListItem): Boolean {
        return (oldItem as? StreamListItem.StreamItem) == (newItem as? StreamListItem.StreamItem)
                || (oldItem as? StreamListItem.TopicItem) == (newItem as? StreamListItem.TopicItem)
    }
}