package com.example.tfs.common.baseadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.tfs.common.baseitems.LoaderItem

@Suppress("UNCHECKED_CAST")
open class BaseAdapter(
    private val items: List<AdapterItemBase<*, *>>,
) : ListAdapter<AdapterItem, BaseViewHolder<View, AdapterItem>>(BaseDiffUtil(items)) {

    var currentData: List<AdapterItem> = mutableListOf()
    var isLoading: Boolean = false

    open fun updateData(data: List<AdapterItem>) {
        currentData = data
        submitList(data)
    }

    open fun addTextShimmerItem(item: AdapterItem, text: String) = Unit

    open fun addHeaderItem(item: AdapterItem) = Unit

    open fun addFooterItem(item: AdapterItem) = Unit

    open fun addData(dataList: List<AdapterItem>) {
        currentData = currentData + dataList
        submitList(currentData)
    }

    //override fun getItemCount() = getDataSize() + if (isLoading) 1 else 0

    //private fun getDataSize() = super.getItemCount()

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

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<View, AdapterItem>) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetached()
    }

    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        return items.find { it.isRelativeItem(item) }
            ?.getLayoutId()
            ?: throw IllegalArgumentException("View type not found: $item")
    }
}
