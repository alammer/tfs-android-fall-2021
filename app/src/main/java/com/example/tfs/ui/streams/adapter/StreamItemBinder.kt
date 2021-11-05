package com.example.tfs.ui.streams.adapter

import com.example.tfs.R
import com.example.tfs.domain.streams.StreamItemList

class StreamItemBinder(private val onClickItem: (StreamItemList) -> Unit) {

    fun bind(streamItemViewHolder: StreamItemViewHolder, item: StreamItemList.StreamItem) {

        streamItemViewHolder.setStreamName(item.name)

        streamItemViewHolder.setStreamExpandIcon(if(item.expanded) R.drawable.ic_collapce else R.drawable.ic_expand)

        streamItemViewHolder.setStreamExpandClickListener(onClickItem, item)
    }
}