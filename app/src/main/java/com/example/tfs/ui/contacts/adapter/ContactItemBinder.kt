package com.example.tfs.ui.contacts.adapter

import com.example.tfs.R
import com.example.tfs.domain.contacts.Contact

class ContactItemBinder(
    private val onContactClick: (Int) -> Unit
) {

    fun bind(contactViewHolder: ContactViewHolder, item: Contact) {

        contactViewHolder.setContactName(item.name)
        contactViewHolder.setContactEmail(item.email)
        contactViewHolder.setContactState(if (item.isActive) R.drawable.ic_offline else R.drawable.ic_online)

        item.avatarUrl?.let {
            contactViewHolder.setContactAvatar(it)
        } ?: contactViewHolder.setContactInitials(item.name)

        contactViewHolder.setContactClickListener(onContactClick, item.id)
    }
}