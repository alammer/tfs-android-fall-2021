package com.example.tfs.ui.topic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.topic.PostItem

class TopicViewAdapter(
    onChangeReactionClick: (messageId: Int, emojiName: String, emojiCode: String) -> Unit,
    onAddReactionClick: (messageId: Int) -> Unit,
) : ListAdapter<PostItem, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private val userPostItemBinder =
        UserPostItemBinder(onChangeReactionClick, onAddReactionClick)
    private val ownerPostItemBinder =
        OwnerPostItemBinder()

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is PostItem.UserPostItem -> R.layout.item_user_message
        is PostItem.OwnerPostItem -> R.layout.item_owner_message
        is PostItem.LocalDateItem -> R.layout.item_topic_rv_date
        else -> throw IllegalStateException("Unknown view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_user_message -> UserPostViewHolder(v)
            R.layout.item_owner_message -> OwnerPostViewHolder(v)
            R.layout.item_topic_rv_date -> DateViewHolder(v)
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserPostViewHolder -> {
                val item = getItem(position) as PostItem.UserPostItem
                userPostItemBinder.bind(holder, item)
            }

            is OwnerPostViewHolder -> {
                val item = getItem(position) as PostItem.OwnerPostItem
                ownerPostItemBinder.bind(holder, item)
            }

            is DateViewHolder -> {
                val item = getItem(position) as PostItem.LocalDateItem
                holder.setDate(item.postDate)
            }
            else -> throw IllegalStateException("Unknown viewHolder")
        }
    }
}

private class MessageDiffCallback : DiffUtil.ItemCallback<PostItem>() {

    override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem) = when (oldItem) {
        is PostItem.LocalDateItem -> oldItem.postDate == (newItem as? PostItem.LocalDateItem)?.postDate
        is PostItem.UserPostItem -> oldItem.timeStamp == (newItem as? PostItem.UserPostItem)?.timeStamp
        is PostItem.OwnerPostItem -> oldItem.timeStamp == (newItem as? PostItem.OwnerPostItem)?.timeStamp
    }


    override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem) = when (oldItem) {
        is PostItem.LocalDateItem -> oldItem == (newItem as? PostItem.LocalDateItem)
        is PostItem.UserPostItem -> oldItem == (newItem as? PostItem.UserPostItem)
        is PostItem.OwnerPostItem -> oldItem == (newItem as? PostItem.OwnerPostItem)
    }
}