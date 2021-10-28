package com.example.tfs.presentation.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.tfs.R
import com.example.tfs.data.Contact

class ContactViewAdapter(private val clickListener: ItemClickListener) :
    ListAdapter<Contact, ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact_rv_user, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)
        holder.contactName.text = item.userName
        holder.contactEmail.text = item.userEmail
        //holder.contactAvatar.setImageResource(R.drawable.bad)
        holder.contactAvatar.getItemData(1, item.userImage, userName = "Ivan Ivanov")
        holder.itemView.setOnClickListener { clickListener.onClick(item) }
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {

    override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem.userId == newItem.userId

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem.userStatus == newItem.userStatus && oldItem.userImage == newItem.userImage
}

class ItemClickListener(val clickListener: (item: Contact) -> Unit) {
    fun onClick(item: Contact) = clickListener(item)
}