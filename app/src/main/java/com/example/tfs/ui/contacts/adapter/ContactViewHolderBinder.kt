package com.example.tfs.ui.contacts.adapter

import com.example.tfs.R
import com.example.tfs.domain.contacts.Contact

class ContactViewHolderBinder(
    private val onContactClick: (Contact) -> Unit
) {

    fun bind(contactViewHolder: ContactViewHolder, item: Contact) {

        contactViewHolder.setContactName(item.userName)
        contactViewHolder.setContactEmail(item.userEmail)
        contactViewHolder.setContactState(if (item.userState == 0) R.drawable.ic_offline else R.drawable.ic_online)

        item.userImage?.let {
            contactViewHolder.setContactAvatar(it)
        } ?: contactViewHolder.setContactInitials(item.userName)

        contactViewHolder.itemView.setOnClickListener { onContactClick(item) }
    }
}