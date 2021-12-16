package com.example.tfs.ui.stream.adapter.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.stream.RelatedTopic

class TopicItem(
    private val onClickTopic: (RelatedTopic) -> Unit
) : AdapterItemBase<View, RelatedTopic> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is RelatedTopic

    override fun getLayoutId() = R.layout.item_related_topic

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, RelatedTopic> {
        val v = layoutInflater.inflate(R.layout.item_related_topic, parent, false)
        return TopicItemViewHolder(v, onClickTopic)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<RelatedTopic> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<RelatedTopic>() {

        override fun areItemsTheSame(oldItem: RelatedTopic, newItem: RelatedTopic) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: RelatedTopic, newItem: RelatedTopic) =
            oldItem == newItem
    }
}