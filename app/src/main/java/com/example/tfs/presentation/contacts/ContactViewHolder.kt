package com.example.tfs.presentation.contacts

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.customviews.UserAvatarView

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val contactAvatar: UserAvatarView = itemView.findViewById(R.id.cvAvatar1)
    private val contactName: TextView = itemView.findViewById<TextView>(R.id.tvContactName)
    private val contactEmail: TextView = itemView.findViewById<TextView>(R.id.tvContactEmail)

    fun setContactAvatar (userAvatarUri: Int, userState: Int, userName: String) {
        contactAvatar.setAvatar(userAvatarUri, userState, userName)
    }

    fun setContactName (userName: String) {
        contactName.text = userName
    }

    fun setContactEmail (userEmail: String) {
        contactEmail.text = userEmail
    }
}