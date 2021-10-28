package com.example.tfs.presentation.contacts

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.presentation.topic.customviews.UserAvatarView

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val contactAvatar: UserAvatarView = itemView.findViewById(R.id.cvAvatar1)
    val contactName: TextView = itemView.findViewById<TextView>(R.id.tvContactName)
    val contactEmail: TextView = itemView.findViewById<TextView>(R.id.tvContactEmail)
}