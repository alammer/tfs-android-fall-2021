package com.example.tfs.ui.contacts.adapter.items

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tfs.R
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.database.entity.LocalUser
import com.example.tfs.util.drawUserInitials
import com.example.tfs.util.toPx
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ContactItemViewHolder(
    private val contactView: View,
    private val onContactClick: (Int) -> Unit
) : BaseViewHolder<View, LocalUser>(contactView) {

    private val contactAvatar: ShapeableImageView =
        contactView.findViewById(R.id.imgContactAvatar)
    private val contactState: ImageView = contactView.findViewById(R.id.imgContactState)
    private val contactName: TextView = contactView.findViewById(R.id.tvContactName)
    private val contactEmail: TextView = contactView.findViewById(R.id.tvContactEmail)

    override fun onBind(item: LocalUser) {
        super.onBind(item)
        contactName.text = item.userName
        contactEmail.text = item.email
        setContactState(item.userState)
        item.avatarUrl?.let {
            setContactAvatar(it)
        } ?: setContactInitials(item.userName)
    }

    override fun onBind(item: LocalUser, payloads: List<Any>) {
        super.onBind(item, payloads)
    }

    private fun setContactState(state: String) {
        contactState.setImageResource(
            when (state) {
                "active" -> R.drawable.ic_status_online
                "idle" -> R.drawable.ic_status_idle
                else -> R.drawable.ic_status_offline
            }
        )
    }

    private fun setContactAvatar(avatarUrl: String) {
        Glide.with(contactView)
            .load(avatarUrl)
            .centerCrop()
            .placeholder(R.drawable.loading_img_animation)
            .error(R.drawable.broken_img)
            .into(contactAvatar)
            .waitForLayout()
    }

    private fun setContactInitials(userName: String) {
        contactAvatar.drawUserInitials(userName)
    }

}


class ContactViewHolder(private val contactItemView: View) :
    RecyclerView.ViewHolder(contactItemView) {

    private val contactAvatar: ShapeableImageView =
        contactItemView.findViewById(R.id.imgContactAvatar)
    private val contactState: ImageView = contactItemView.findViewById(R.id.imgContactState)
    private val contactName: TextView = contactItemView.findViewById<TextView>(R.id.tvContactName)
    private val contactEmail: TextView = contactItemView.findViewById<TextView>(R.id.tvContactEmail)

    fun setContactAvatar(userAvatarUrl: String) {
        Picasso.get().load(userAvatarUrl)
            .resize(CONTACT_AVATAR_WIDTH.toPx, CONTACT_AVATAR_WIDTH.toPx)
            .centerCrop().into(contactAvatar)
    }

    fun setContactState(userState: Int) {
        contactState.setImageResource(userState)
    }

    fun setContactInitials(userName: String) {
        contactAvatar.drawUserInitials(userName)
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

class ContactItemBinder(
    private val onContactClick: (Int) -> Unit,
) {

    fun bind(contactViewHolder: ContactViewHolder, item: LocalUser) {

        contactViewHolder.setContactName(item.userName)
        contactViewHolder.setContactEmail(item.email)
        contactViewHolder.setContactState(
            when (item.userState) {
                "active" -> R.drawable.ic_status_online
                "idle" -> R.drawable.ic_status_idle
                else -> R.drawable.ic_status_offline
            }
        )

        item.avatarUrl?.let {
            contactViewHolder.setContactAvatar(it)
        } ?: contactViewHolder.setContactInitials(item.userName)

        contactViewHolder.setContactClickListener(onContactClick, item.id)
    }
}

