package com.example.tfs.ui.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.TopicCell
import com.example.tfs.presentation.topic.DateViewHolder
import com.example.tfs.presentation.topic.PostViewHolder
import com.example.tfs.presentation.topic.TopicAdapterCallback


class TopicViewAdapter :
    ListAdapter<TopicCell, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private var recyclerViewCallback: TopicAdapterCallback? = null

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is TopicCell.PostCell -> R.layout.item_topic_rv_post
        is TopicCell.LocalDateCell -> R.layout.item_topic_rv_date
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
                val item = getItem(position) as TopicCell.PostCell

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
                val item = getItem(position) as TopicCell.LocalDateCell
                holder.dateView.text = item.postDate

            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }

    }

    fun setOnCallbackListener(recyclerViewCallback: TopicAdapterCallback) {
        this.recyclerViewCallback = recyclerViewCallback
    }
}

private class MessageDiffCallback : DiffUtil.ItemCallback<TopicCell>() {

    override fun areItemsTheSame(oldItem: TopicCell, newItem: TopicCell): Boolean {

        val isSameDateItem = oldItem is TopicCell.LocalDateCell
                && newItem is TopicCell.LocalDateCell
                && oldItem.postDate == newItem.postDate

        val isSamePostItem = oldItem is TopicCell.PostCell
                && newItem is TopicCell.PostCell
                && oldItem.message == newItem.message

        return isSameDateItem || isSamePostItem
    }

    override fun areContentsTheSame(oldItem: TopicCell, newItem: TopicCell) = oldItem == newItem
}