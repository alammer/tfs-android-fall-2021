package com.example.tfs.presentation.contacts

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val contactAvatar: ImageView = itemView.findViewById<ImageView>(R.id.imgContactAvatar)
    val contactStatus: ImageView = itemView.findViewById<ImageView>(R.id.imgContactStatus)
    val contactName: TextView = itemView.findViewById<TextView>(R.id.tvContactName)
    val contactEmail: TextView = itemView.findViewById<TextView>(R.id.tvContactEmail)
}