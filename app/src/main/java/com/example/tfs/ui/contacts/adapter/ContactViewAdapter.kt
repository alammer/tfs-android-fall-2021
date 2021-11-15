package com.example.tfs.ui.contacts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.tfs.R
import com.example.tfs.network.models.User

class ContactViewAdapter(onContactClick: (Int) -> Unit) :
    ListAdapter<User, ContactViewHolder>(ContactDiffCallback()) {

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

private class ContactDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User) =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: User, newItem: User) =
        oldItem == newItem
}