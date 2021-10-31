package com.example.tfs.presentation.topic.emoji

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class EmojiRecyclerAdapter(private val onEmojiClick: (Int) -> Unit) :
    ListAdapter<Int, EmojiRecyclerAdapter.EmojiViewHolder>(EmojiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bsd_emoji_item, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val item = getItem(position)
        holder.emojiTextView.text = StringBuilder().appendCodePoint(item).toString()
        holder.emojiTextView.setOnClickListener { onEmojiClick(item) }
    }

    class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiTextView: TextView = itemView.findViewById<TextView>(R.id.tvEmojiItem)
    }
}

private class EmojiDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(oldItem: Int, newItem: Int) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Int, newItem: Int) = oldItem == newItem
}