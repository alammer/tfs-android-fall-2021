package com.example.tfs.presentation.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.tfs.R
import com.example.tfs.data.Contact

class ContactViewAdapter(private val onItemClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact_rv_user, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)
        holder.setContactName(item.userName)
        holder.setContactEmail(item.userEmail)
        holder.setContactAvatar(item.userImage, item.userState, item.userName)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {

    override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem.userId == newItem.userId

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem == newItem
}