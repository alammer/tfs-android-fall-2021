package com.example.tfs.ui.topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.Post
import com.example.tfs.customviews.PostLayout

class TopicViewAdapter :
    ListAdapter<Post, TopicViewAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private var recyclerViewCallback: TopicAdapterCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        holder.postView.createLayout(item)

        val childOffset = if (item.isOwner) 0 else 1

        holder.postView.getChildAt(childOffset).setOnLongClickListener {
            this.recyclerViewCallback?.onRecycleViewLongPress(position)
            return@setOnLongClickListener true
        }

        holder.postView.getChildAt(childOffset + 1)?.let { emojiGroup ->
            if (emojiGroup is ViewGroup && emojiGroup.childCount > 1) {
                (0 until emojiGroup.childCount - 1).forEach { emojiPosition ->
                    emojiGroup.getChildAt(emojiPosition).setOnClickListener {
                        this.recyclerViewCallback?.onRecycleViewItemClick(position, emojiPosition)
                    }
                }
                //click on "+"
                emojiGroup.getChildAt(emojiGroup.childCount - 1).setOnClickListener {
                    this.recyclerViewCallback?.onRecycleViewLongPress(position)
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

private class MessageDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Post, newItem: Post) =
        oldItem.reaction == newItem.reaction
}