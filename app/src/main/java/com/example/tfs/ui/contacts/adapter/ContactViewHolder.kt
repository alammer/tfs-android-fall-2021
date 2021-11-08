package com.example.tfs.ui.contacts.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.drawUserInitials
import com.google.android.material.imageview.ShapeableImageView

class ContactViewHolder(private val contactItemView: View) :
    RecyclerView.ViewHolder(contactItemView) {

    private val contactAvatar: ShapeableImageView =
        contactItemView.findViewById(R.id.imgContactAvatar)
    private val contactState: ImageView = contactItemView.findViewById(R.id.imgContactState)
    private val contactName: TextView = contactItemView.findViewById<TextView>(R.id.tvContactName)
    private val contactEmail: TextView = contactItemView.findViewById<TextView>(R.id.tvContactEmail)

    fun setContactAvatar(userAvatarUrl: String) {
        contactAvatar.setImageURI(userAvatarUrl.toUri())
    }

    fun setContactState(userState: Int) {
        contactState.setImageResource(userState)
    }

    fun setContactInitials(userName: String) {
        contactAvatar.drawUserInitials(userName, CONTACT_AVATAR_WIDTH.dpToPixels())
    }

    fun setContactName(userName: String) {
        contactName.text = userName
    }

    fun setContactEmail(userEmail: String) {
        contactEmail.text = userEmail
    }

    fun setContactClickListener(selectContact: (Int) -> Unit, contactId: Int) {
        contactItemView.setOnClickListener { selectContact(contactId) }
    }
}

private const val CONTACT_AVATAR_WIDTH = 64