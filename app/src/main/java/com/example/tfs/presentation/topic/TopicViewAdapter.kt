package com.example.tfs.presentation.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.TopicItem

class TopicViewAdapter(
    onChangeReactionClick: (TopicItem, Int) -> Unit,
    onAddReactionClick: (TopicItem) -> Unit
) : ListAdapter<TopicItem, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private val userPostItemBinder =
        UserPostViewHolderBinder(onChangeReactionClick, onAddReactionClick)
    private val ownerPostItemBinder =
        OwnerPostViewHolderBinder()

    private var recyclerViewCallback: TopicAdapterCallback? = null

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is TopicItem.UserPostItem -> R.layout.item_topic_rv_user_post
        is TopicItem.OwnerPostItem -> R.layout.item_topic_rv_owner_post
        is TopicItem.LocalDateItem -> R.layout.item_topic_rv_date
        null -> throw IllegalStateException("Unknown view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_topic_rv_user_post -> UserPostViewHolder(v)
            R.layout.item_topic_rv_owner_post -> OwnerPostViewHolder(v)
            R.layout.item_topic_rv_date -> DateViewHolder(v)
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserPostViewHolder -> {
                val item = getItem(position) as TopicItem.UserPostItem
                userPostItemBinder.bind(holder, item)
            }

            is OwnerPostViewHolder -> {
                val item = getItem(position) as TopicItem.OwnerPostItem
                ownerPostItemBinder.bind(holder, item)
            }

            is DateViewHolder -> {
                val item = getItem(position) as TopicItem.LocalDateItem
                holder.setDate(item.postDate)

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
                || (oldItem as? TopicItem.UserPostItem)?.timeStamp == (newItem as? TopicItem.UserPostItem)?.timeStamp
    }

    override fun areContentsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem is TopicItem.LocalDateItem == newItem is TopicItem.LocalDateItem
                || (oldItem as? TopicItem.UserPostItem) == (newItem as? TopicItem.UserPostItem)
    }
}