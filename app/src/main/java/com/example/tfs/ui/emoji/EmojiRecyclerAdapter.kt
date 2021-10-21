package com.example.tfs.ui.emoji

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class EmojiRecyclerAdapter(private val clickListener: EmojiClickListener) :
    ListAdapter<Int, EmojiRecyclerAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bsd_emoji_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)
        holder.emojiTextView.text = StringBuilder().appendCodePoint(item).toString()
        holder.emojiTextView.setOnClickListener { clickListener.onClick(item) }
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiTextView: TextView = itemView.findViewById<TextView>(R.id.tvEmojiItem)
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(oldItem: Int, newItem: Int) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Int, newItem: Int) = oldItem == newItem
}

class EmojiClickListener(val clickListener: (emoji: Int) -> Unit) {

    fun onClick(emoji: Int) = clickListener(emoji)
}