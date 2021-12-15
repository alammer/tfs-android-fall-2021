package com.example.tfs.ui.stream.adapter.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.streams.DomainTopic

class TopicItem(
    private val onClickTopic: (DomainTopic) -> Unit
) : AdapterItemBase<View, DomainTopic> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is DomainTopic

    override fun getLayoutId() = R.layout.item_related_topic

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, DomainTopic> {
        val v = layoutInflater.inflate(R.layout.item_related_topic, parent, false)
        return TopicItemViewHolder(v, onClickTopic)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<DomainTopic> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DomainTopic>() {

        override fun areItemsTheSame(oldItem: DomainTopic, newItem: DomainTopic) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: DomainTopic, newItem: DomainTopic) =
            oldItem == newItem
    }
}