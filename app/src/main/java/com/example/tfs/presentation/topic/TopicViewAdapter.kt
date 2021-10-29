package com.example.tfs.presentation.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.TopicItem

class TopicViewAdapter :
    ListAdapter<TopicItem, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private var recyclerViewCallback: TopicAdapterCallback? = null

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is TopicItem.PostItem -> R.layout.item_topic_rv_post
        is TopicItem.LocalDateItem -> R.layout.item_topic_rv_date
        null -> throw IllegalStateException("Unknown view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_topic_rv_post -> PostViewHolder(v)
            R.layout.item_topic_rv_date -> DateViewHolder(v)
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val item = getItem(position) as TopicItem.PostItem

                holder.postView.createLayout(item)

                val childOffset = if (item.isOwner) 0 else 1

                if (!item.isOwner) {
                    holder.postView.getChildAt(childOffset).setOnLongClickListener {
                        this.recyclerViewCallback?.onRecycleViewLongPress(position)
                        return@setOnLongClickListener true
                    }

                    holder.postView.getChildAt(childOffset + 1)?.let { emojiGroup ->
                        if (emojiGroup is ViewGroup && emojiGroup.childCount > 1) {
                            (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
                                emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                                    this.recyclerViewCallback?.onRecycleViewItemClick(
                                        position,
                                        item.reaction[emojiPosition].emoji
                                    )
                                }
                            }
                            //click on "+"
                            emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
                                this.recyclerViewCallback?.onRecycleViewLongPress(position)
                            }
                        }
                    }
                }
            }
            is DateViewHolder -> {
                val item = getItem(position) as TopicItem.LocalDateItem
                holder.dateView.text = item.postDate

            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }

    }

    fun setOnCallbackListener(recyclerViewCallback: TopicAdapterCallback) {
        this.recyclerViewCallback = recyclerViewCallback
    }
}

private class MessageDiffCallback : DiffUtil.ItemCallback<TopicItem>() {

    override fun areItemsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return (oldItem as? TopicItem.LocalDateItem)?.postDate == (newItem as? TopicItem.LocalDateItem)?.postDate
                || (oldItem as? TopicItem.PostItem)?.timeStamp == (newItem as? TopicItem.PostItem)?.timeStamp
    }

    override fun areContentsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem is TopicItem.LocalDateItem == newItem is TopicItem.LocalDateItem
                || (oldItem as? TopicItem.PostItem) == (newItem as? TopicItem.PostItem)
    }
}