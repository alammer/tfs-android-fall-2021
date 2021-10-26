package com.example.tfs.presentation.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.Contact
import com.example.tfs.data.StreamCell
import com.example.tfs.presentation.streams.StreamViewHolder
import com.example.tfs.presentation.streams.TopicViewHolder

class ContactViewAdapter(private val clickListener: ItemClickListener):
ListAdapter<ContactViewHolder, RecyclerView.ViewHolder>(StreamDiffCallback()) {

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
                Log.i("StreamViewAdapter", "Function called: 1")
                val item = getItem(position) as StreamCell.StreamNameCell
                holder.streamName.text = item.streamName
                if (item.expanded) { holder.btnTopicList.setImageResource(R.drawable.ic_collapce) } else {
                    holder.btnTopicList.setImageResource(R.drawable.ic_expand)
                }
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

private class ContactCallback : DiffUtil.ItemCallback<ContactViewHolder>() {
    override fun areItemsTheSame(oldItem: ContactViewHolder, newItem: ContactViewHolder): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ContactViewHolder, newItem: ContactViewHolder): Boolean {
        return oldItem.contactName == newItem.contactName
    }

}

class ItemClickListener(val clickListener: (item: Contact) -> Unit) {
    fun onClick(item: Contact) = clickListener(item)
}