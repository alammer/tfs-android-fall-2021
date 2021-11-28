package com.example.tfs.ui.stream.adapter

import com.example.tfs.R
import com.example.tfs.domain.streams.StreamListItem

class StreamItemBinder(private val onClickItem: (StreamListItem) -> Unit) {

    fun bind(streamItemViewHolder: StreamItemViewHolder, item: StreamListItem.StreamItem) {

        streamItemViewHolder.setStreamName(item.name)

        streamItemViewHolder.setStreamExpandIcon(if (item.expanded) R.drawable.ic_collapce else R.drawable.ic_expand)

        streamItemViewHolder.setStreamExpandClickListener(onClickItem, item)
    }
}