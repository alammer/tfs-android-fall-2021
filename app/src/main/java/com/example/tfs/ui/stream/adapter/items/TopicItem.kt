package com.example.tfs.ui.stream.adapter.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.domain.streams.DomainTopic
import com.example.tfs.ui.stream.adapter.base.BaseViewHolder
import com.example.tfs.ui.stream.adapter.base.StreamListItem
import com.example.tfs.ui.stream.adapter.base.StreamListItemBase

class TopicItem (
    private val onClickTopic: (DomainTopic) -> Unit
) : StreamListItemBase<View, DomainTopic> {

    override fun isRelativeItem(streamListItem: StreamListItem) = streamListItem is DomainTopic

    override fun getLayoutId() = R.layout.item_stream_rv_topic

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, DomainTopic> {
        val v = layoutInflater.inflate(R.layout.item_stream_rv_topic, parent, false)
        return TopicItemViewHolder(v, onClickTopic)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DomainTopic>() {

        override fun areItemsTheSame(oldItem: DomainTopic, newItem: DomainTopic) = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: DomainTopic, newItem: DomainTopic) = oldItem == newItem
    }
}