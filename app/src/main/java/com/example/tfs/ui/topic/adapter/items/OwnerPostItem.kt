package com.example.tfs.ui.topic.adapter.items

import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.topic.DomainOwnerPost

class OwnerPostItem(
    private val onPostTap: (postId: Int, isOwner: Boolean) -> Unit
) : AdapterItemBase<View, DomainOwnerPost> {

    private val spannableFactory = object : Spannable.Factory() {
        override fun newSpannable(source: CharSequence?): Spannable {
            return source as Spannable
        }
    }

    override fun isRelativeItem(item: AdapterItem): Boolean = item is DomainOwnerPost

    override fun getLayoutId() = R.layout.item_post_owner

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, DomainOwnerPost> {
        val v = layoutInflater.inflate(R.layout.item_post_owner, parent, false)
        return OwnerPostItemViewHolder(v, onPostTap, spannableFactory)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<DomainOwnerPost> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DomainOwnerPost>() {

        override fun areItemsTheSame(oldItem: DomainOwnerPost, newItem: DomainOwnerPost) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DomainOwnerPost, newItem: DomainOwnerPost) =
            oldItem == newItem
    }
}