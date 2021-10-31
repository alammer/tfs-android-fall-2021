package com.example.tfs.presentation.streams

import com.example.tfs.R
import com.example.tfs.data.StreamItemList

class StreamItemViewHolderBinder(private val onClickItem: (StreamItemList) -> Unit) {

    fun bind(streamItemViewHolder: StreamItemViewHolder, item: StreamItemList.StreamItem) {

        streamItemViewHolder.setStreamName(item.streamName)

        streamItemViewHolder.setStreamExpandIcon(if(item.expanded) R.drawable.ic_collapce else R.drawable.ic_expand)

        streamItemViewHolder.setStreamExpandClickListener(onClickItem)
    }
}