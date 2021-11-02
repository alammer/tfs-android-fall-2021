package com.example.tfs.presentation.contacts

import com.example.tfs.R
import com.example.tfs.data.Contact

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