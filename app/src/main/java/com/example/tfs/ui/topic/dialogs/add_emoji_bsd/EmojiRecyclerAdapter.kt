package com.example.tfs.ui.topic.dialogs.add_emoji_bsd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class EmojiRecyclerAdapter(private val onEmojiClick: (String) -> Unit) :
    ListAdapter<String, EmojiRecyclerAdapter.EmojiViewHolder>(EmojiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.emoji_bsd_item, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val item = getItem(position)
        holder.emojiTextView.text = item
        holder.emojiTextView.setOnClickListener { onEmojiClick(item) }
    }

    class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiTextView: TextView = itemView.findViewById<TextView>(R.id.tvEmojiItem)
    }
}

private class EmojiDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
}