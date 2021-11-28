package com.example.tfs.ui.stream.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.streams.StreamListItem


class StreamViewAdapter(onItemClicked: (StreamListItem) -> Unit) :
    ListAdapter<StreamListItem, RecyclerView.ViewHolder>(StreamDiffCallback()) {

    private val streamItemBinder =
        StreamItemBinder(onItemClicked)

    private val topicItemBinder =
        TopicItemBinder(onItemClicked)

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is StreamListItem.StreamItem -> R.layout.item_stream_rv_header
        is StreamListItem.TopicItem -> R.layout.item_stream_rv_topic
        else -> throw IllegalStateException("Unknown view")
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
                streamItemBinder.bind(holder, item)
            }
            is TopicItemViewHolder -> {
                val item = getItem(position) as StreamListItem.TopicItem
                topicItemBinder.bind(holder, item)
            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }
    }
}

private class StreamDiffCallback : DiffUtil.ItemCallback<StreamListItem>() {

    override fun areItemsTheSame(oldItem: StreamListItem, newItem: StreamListItem) =
        when (oldItem) {
            is StreamListItem.StreamItem -> oldItem.name == (newItem as? StreamListItem.StreamItem)?.name
            is StreamListItem.TopicItem -> oldItem.name == (newItem as? StreamListItem.TopicItem)?.name
        }


    override fun areContentsTheSame(oldItem: StreamListItem, newItem: StreamListItem) =
        when (oldItem) {
            is StreamListItem.StreamItem -> oldItem == (newItem as? StreamListItem.StreamItem)
            is StreamListItem.TopicItem -> oldItem == (newItem as? StreamListItem.TopicItem)
        }
}