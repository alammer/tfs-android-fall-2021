package com.example.tfs.ui.topic

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.customviews.PostLayout
import com.example.tfs.customviews.Post

class TopicViewAdapter :
    androidx.recyclerview.widget.ListAdapter<Post, TopicViewAdapter.MessageViewHolder>(ContactDiffCallback()) {

    var recyclerViewCallback: TopicAdapterCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        holder.postView.createLayout(item)

        holder.postView.getChildAt(1).setOnLongClickListener {
            this.recyclerViewCallback?.onRecycleViewLongPress(position)
            return@setOnLongClickListener true
        }

        holder.postView.getChildAt(2)?.let { emojiGroup ->
            if (emojiGroup is ViewGroup) {
                (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
                    emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                        this.recyclerViewCallback?.onRecycleViewItemClick(position, emojiPosition)
                    }
                }
                //click on "+"
                emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
                    this.recyclerViewCallback?.onRecycleViewItemClick(position, 555)//this.recyclerViewCallback?.onRecycleViewLongPress(position)
                }
            }
        }

    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postView: PostLayout = itemView.findViewById<PostLayout>(R.id.cvPost)
    }

    fun setOnCallbackListener(recyclerViewCallback: TopicAdapterCallback) {
        this.recyclerViewCallback = recyclerViewCallback
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.message == newItem.message

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem.equals(newItem)
}