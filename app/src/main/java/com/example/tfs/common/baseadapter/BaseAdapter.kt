package com.example.tfs.common.baseadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter


abstract class BaseAdapter(
    private val items: List<AdapterItemBase<*, *>>,
) : ListAdapter<AdapterItem, BaseViewHolder<View, AdapterItem>>(BaseDiffUtil(items)) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<View, AdapterItem> {
        val inflater = LayoutInflater.from(parent.context)
        return items.find { it.getLayoutId() == viewType }
            ?.getViewHolder(inflater, parent)
            ?.let { it as BaseViewHolder<View, AdapterItem> }
            ?: throw IllegalArgumentException("View type not found: $viewType")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<View, AdapterItem>, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<View, AdapterItem>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.onBind(currentList[position], payloads)
        }
    }

    override fun getItemViewType(position: Int): Int { //TODO("IndexOutOfBound rise here in decorator")
        val item = currentList[position]
        return items.find { it.isRelativeItem(item) }
            ?.getLayoutId()
            ?: throw IllegalArgumentException("View type not found: $item")
    }
}
