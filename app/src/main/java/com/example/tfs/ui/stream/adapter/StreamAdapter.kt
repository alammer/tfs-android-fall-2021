package com.example.tfs.ui.stream.adapter

import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseAdapter
import com.example.tfs.common.baseitems.TextShimmer
import com.example.tfs.domain.stream.DomainStream

class StreamAdapter(
    streamItemList: List<AdapterItemBase<*, *>>
) : BaseAdapter(streamItemList) {

    override fun addTextShimmerItem(item: AdapterItem, text: String) {
        super.addTextShimmerItem(item, text)

        if (item is DomainStream) {
            val newList = currentData.toMutableList()
            val shimmerPosition = currentData.indexOf(item)
            newList.removeAt(shimmerPosition)
            newList.add(shimmerPosition, item.copy(updated = true))
            newList.add(shimmerPosition + 1, TextShimmer(text))
            updateData(newList.toList())
        }
    }
}