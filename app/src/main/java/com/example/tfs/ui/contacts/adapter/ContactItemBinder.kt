package com.example.tfs.ui.contacts.adapter

import com.example.tfs.R
import com.example.tfs.database.entity.LocalUser
import com.example.tfs.network.models.User

class ContactItemBinder(
    private val onContactClick: (Int) -> Unit,
) {

    fun bind(contactViewHolder: ContactViewHolder, item: LocalUser) {

        contactViewHolder.setContactName(item.userName)
        contactViewHolder.setContactEmail(item.email)
        contactViewHolder.setContactState(if (item.isActive) R.drawable.ic_offline else R.drawable.ic_online)

        item.avatarUrl?.let {
            contactViewHolder.setContactAvatar(it)
        } ?: contactViewHolder.setContactInitials(item.userName)

        contactViewHolder.setContactClickListener(onContactClick, item.id)
    }
}