package com.example.tfs.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.model.UserContact

class ContactListAdapter : ListAdapter<UserContact, ContactListAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)
        holder.contactTextView.text = holder.itemView.context.getString(R.string.user_contact_template, item.name, item.phoneNumber)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactTextView: TextView = itemView.findViewById<TextView>(R.id.tvContactItem)
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<UserContact>() {
    override fun areItemsTheSame(oldItem: UserContact, newItem: UserContact) = oldItem.phoneNumber == newItem.phoneNumber

    override fun areContentsTheSame(oldItem: UserContact, newItem: UserContact) = oldItem == newItem
}