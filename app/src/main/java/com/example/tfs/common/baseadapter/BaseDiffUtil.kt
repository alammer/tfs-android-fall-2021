package com.example.tfs.common.baseadapter

import androidx.recyclerview.widget.DiffUtil

internal class BaseDiffUtil(
    private val items: List<AdapterItemBase<*, *>>,
) : DiffUtil.ItemCallback<AdapterItem>() {

    override fun areItemsTheSame(
        oldStreamListItem: AdapterItem,
        newStreamListItem: AdapterItem
    ): Boolean {
        if (oldStreamListItem::class != newStreamListItem::class) return false

        return getItemCallback(oldStreamListItem).areItemsTheSame(
            oldStreamListItem,
            newStreamListItem
        )
    }

    override fun areContentsTheSame(
        oldStreamListItem: AdapterItem,
        newStreamListItem: AdapterItem
    ): Boolean {
        if (oldStreamListItem::class != newStreamListItem::class) return false

        return getItemCallback(oldStreamListItem).areContentsTheSame(
            oldStreamListItem,
            newStreamListItem
        )
    }

/*    override fun getChangePayload(oldStreamListItem: AdapterItem, newStreamListItem: AdapterItem): Any? {
        if (oldStreamListItem::class != newStreamListItem::class) return false

        return getItemCallback(oldStreamListItem).getChangePayload(oldStreamListItem, newStreamListItem)
    }*/

    @Suppress("UNCHECKED_CAST")
    private fun getItemCallback(
        item: AdapterItem
    ): DiffUtil.ItemCallback<AdapterItem> = items.find { it.isRelativeItem(item) }
        ?.getDiffUtil()
        ?.let { it as DiffUtil.ItemCallback<AdapterItem> }
        ?: throw IllegalStateException("DiffUtil not found for $item")
}