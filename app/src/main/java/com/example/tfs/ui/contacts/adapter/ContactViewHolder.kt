package com.example.tfs.ui.contacts.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.util.dpToPixels
import com.example.tfs.util.drawUserInitials
import com.google.android.material.imageview.ShapeableImageView

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private val contactAvatar: ShapeableImageView = itemView.findViewById(R.id.imgContactAvatar)
    private val contactState: ImageView = itemView.findViewById(R.id.imgContactState)
    private val contactName: TextView = itemView.findViewById<TextView>(R.id.tvContactName)
    private val contactEmail: TextView = itemView.findViewById<TextView>(R.id.tvContactEmail)

    fun setContactAvatar(userAvatarUri: Int) {
        contactAvatar.setImageResource(userAvatarUri)
    }

    fun setContactState(userState: Int) {
        contactState.setImageResource(userState)
    }

    fun setContactInitials (userName: String) {
        contactAvatar.drawUserInitials(userName, CONTACT_AVATAR_WIDTH.dpToPixels())
    }

    fun setContactName(userName: String) {
        contactName.text = userName
    }

    fun setContactEmail(userEmail: String) {
        contactEmail.text = userEmail
    }
}

private const val CONTACT_AVATAR_WIDTH = 64