package com.example.tfs.ui.streams.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.streams.StreamItemList


class StreamViewAdapter(onItemClicked: (StreamItemList) -> Unit) :
    ListAdapter<StreamItemList, RecyclerView.ViewHolder>(StreamDiffCallback()) {

    private val streamItemBinder =
        StreamItemBinder(onItemClicked)

    private val topicItemBinder =
        TopicItemBinder(onItemClicked)

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is StreamItemList.StreamItem -> R.layout.item_stream_rv_header
        is StreamItemList.TopicItem -> R.layout.item_stream_rv_topic
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
                val item = getItem(position) as StreamItemList.StreamItem
                streamItemBinder.bind(holder, item)
            }
            is TopicItemViewHolder -> {
                val item = getItem(position) as StreamItemList.TopicItem
                topicItemBinder.bind(holder, item)
            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }
    }
}

private class StreamDiffCallback : DiffUtil.ItemCallback<StreamItemList>() {

    override fun areItemsTheSame(oldItem: StreamItemList, newItem: StreamItemList) =
        when (oldItem) {
            is StreamItemList.StreamItem -> oldItem.name == (newItem as? StreamItemList.StreamItem)?.name
            is StreamItemList.TopicItem -> oldItem.name == (newItem as? StreamItemList.TopicItem)?.name
        }


    override fun areContentsTheSame(oldItem: StreamItemList, newItem: StreamItemList) =
        when (oldItem) {
            is StreamItemList.StreamItem -> oldItem == (newItem as? StreamItemList.StreamItem)
            is StreamItemList.TopicItem -> oldItem == (newItem as? StreamItemList.TopicItem)
        }
}