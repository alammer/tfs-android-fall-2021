package com.example.tfs.ui.stream.adapter.base

import androidx.recyclerview.widget.DiffUtil

class BaseDiffUtil(
    private val streamItems: List<StreamListItemBase<*, *>>,
) : DiffUtil.ItemCallback<StreamListItem>() {

    override fun areItemsTheSame(oldStreamListItem: StreamListItem, newStreamListItem: StreamListItem): Boolean {
        if (oldStreamListItem::class != newStreamListItem::class) return false

        return getItemCallback(oldStreamListItem).areItemsTheSame(oldStreamListItem, newStreamListItem)
    }

    override fun areContentsTheSame(oldStreamListItem: StreamListItem, newStreamListItem: StreamListItem): Boolean {
        if (oldStreamListItem::class != newStreamListItem::class) return false

        return getItemCallback(oldStreamListItem).areContentsTheSame(oldStreamListItem, newStreamListItem)
    }

    override fun getChangePayload(oldStreamListItem: StreamListItem, newStreamListItem: StreamListItem): Any? {
        if (oldStreamListItem::class != newStreamListItem::class) return false

        return getItemCallback(oldStreamListItem).getChangePayload(oldStreamListItem, newStreamListItem)
    }

    private fun getItemCallback(
        streamListItem: StreamListItem
    ): DiffUtil.ItemCallback<StreamListItem> = streamItems.find { it.isRelativeItem(streamListItem) }
        ?.getDiffUtil()
        ?.let { it as DiffUtil.ItemCallback<StreamListItem> }
        ?: throw IllegalStateException("DiffUtil not found for $streamListItem")

}