package com.example.tfs.ui.stream.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.tfs.ui.stream.adapter.base.BaseDiffUtil
import com.example.tfs.ui.stream.adapter.base.BaseViewHolder
import com.example.tfs.ui.stream.adapter.base.StreamListItem
import com.example.tfs.ui.stream.adapter.base.StreamListItemBase


class StreamAdapter(
    private val streamItems: List<StreamListItemBase<*, *>>,
) :
    ListAdapter<StreamListItem, BaseViewHolder<View, StreamListItem>>(BaseDiffUtil(streamItems)) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<View, StreamListItem> {
        val inflater = LayoutInflater.from(parent.context)
        return streamItems.find { it.getLayoutId() == viewType }
            ?.getViewHolder(inflater, parent)
            ?.let { it as BaseViewHolder<View, StreamListItem> }
            ?: throw IllegalArgumentException("View type not found: $viewType")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<View, StreamListItem>, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<View, StreamListItem>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.onBind(currentList[position], payloads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        return streamItems.find { it.isRelativeItem(item) }
            ?.getLayoutId()
            ?: throw IllegalArgumentException("View type not found: $item")
    }
}
