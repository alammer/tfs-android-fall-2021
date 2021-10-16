package com.example.tfs.ui.topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.customviews.PostLayout
import com.example.tfs.customviews.Post

class TopicViewAdapter : ListAdapter<Post, TopicViewAdapter.MessageViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        holder.postView.createLayout(item)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postView: PostLayout = itemView.findViewById<PostLayout>(R.id.cvPost)
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.message == newItem.message

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem.equals(newItem)
}