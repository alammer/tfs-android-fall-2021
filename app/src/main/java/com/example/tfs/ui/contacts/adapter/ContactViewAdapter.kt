package com.example.tfs.ui.contacts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.tfs.R
import com.example.tfs.domain.contacts.Contact

class ContactViewAdapter(onContactClick: (Int) -> Unit) :
    ListAdapter<Contact, ContactViewHolder>(ContactDiffCallback()) {

    private val contactItemBinder =
        ContactItemBinder(onContactClick)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact_rv_user, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)
        contactItemBinder.bind(holder, item)
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {

    override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact) =
        oldItem == newItem
}