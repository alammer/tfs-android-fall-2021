package com.example.tfs.ui.stream.adapter.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.domain.streams.DomainStream
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder

class StreamItem(
    private val onClickStream: (Int) -> Unit
) : AdapterItemBase<View, DomainStream> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is DomainStream

    override fun getLayoutId() = R.layout.item_stream_rv_header

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, DomainStream> {
        val v = layoutInflater.inflate(R.layout.item_stream_rv_header, parent, false)
        return StreamItemViewHolder(v, onClickStream)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<DomainStream> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DomainStream>() {

        override fun areItemsTheSame(oldItem: DomainStream, newItem: DomainStream) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DomainStream, newItem: DomainStream) =
            oldItem == newItem

/*        override fun getChangePayload(oldItem: DomainStream, newItem: DomainStream): Any? {
            if (oldItem.expanded != newItem.expanded) return newItem.expanded
            return super.getChangePayload(oldItem, newItem)
        }*/
    }
}