package com.example.tfs.common.baseadapter

import androidx.recyclerview.widget.DiffUtil

internal class BaseDiffUtil(
    private val items: List<AdapterItemBase<*, *>>,
) : DiffUtil.ItemCallback<AdapterItem>() {

    override fun areItemsTheSame(
        oldItem: AdapterItem,
        newItem: AdapterItem
    ): Boolean {
        if (oldItem::class != newItem::class) return false

        return getItemCallback(oldItem).areItemsTheSame(
            oldItem,
            newItem
        )
    }

    override fun areContentsTheSame(
        oldItem: AdapterItem,
        newItem: AdapterItem
    ): Boolean {
        if (oldItem::class != newItem::class) return false

        return getItemCallback(oldItem).areContentsTheSame(oldItem, newItem)
    }

    override fun getChangePayload(oldItem: AdapterItem, newItem: AdapterItem): Any? {
        if (oldItem::class != newItem::class) return false

        return getItemCallback(oldItem).getChangePayload(oldItem, newItem)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getItemCallback(
        item: AdapterItem
    ): DiffUtil.ItemCallback<AdapterItem> = items.find { it.isRelativeItem(item) }
        ?.getDiffUtil()
        ?.let { it as DiffUtil.ItemCallback<AdapterItem> }
        ?: throw IllegalStateException("DiffUtil not found for $item")
}