package com.example.tfs.ui.contacts.adapter.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.database.entity.LocalUser

class ContactItem(
    private val onClickContact: (Int) -> Unit
) : AdapterItemBase<View, LocalUser> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is LocalUser

    override fun getLayoutId() = R.layout.item_contact

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, LocalUser> {
        val v = layoutInflater.inflate(R.layout.item_contact, parent, false)
        return ContactItemViewHolder(v, onClickContact)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<LocalUser> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<LocalUser>() {

        override fun areItemsTheSame(oldItem: LocalUser, newItem: LocalUser) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LocalUser, newItem: LocalUser) =
            oldItem == newItem
    }
}