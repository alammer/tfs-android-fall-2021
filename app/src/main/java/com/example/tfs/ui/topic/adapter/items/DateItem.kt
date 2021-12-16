package com.example.tfs.ui.topic.adapter.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.topic.DomainPostDate

class DateItem
    : AdapterItemBase<View, DomainPostDate> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is DomainPostDate

    override fun getLayoutId() = R.layout.item_post_date

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, DomainPostDate> {
        val v = layoutInflater.inflate(R.layout.item_post_date, parent, false)
        return DateItemViewHolder(v)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<DomainPostDate> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DomainPostDate>() {

        override fun areItemsTheSame(oldItem: DomainPostDate, newItem: DomainPostDate) =
            oldItem.postDate == newItem.postDate

        override fun areContentsTheSame(oldItem: DomainPostDate, newItem: DomainPostDate) =
            oldItem == newItem
    }
}