package com.example.tfs.ui.stream.adapter

import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseAdapter
import com.example.tfs.domain.stream.DomainStream

class StreamAdapter(
    streamItemList: List<AdapterItemBase<*, *>>
) : BaseAdapter(streamItemList) {

    fun addBackgroundShimmer(item: AdapterItem) {
        if (item is DomainStream) {
            val newList = currentData.toMutableList()
            val shimmerPosition = currentData.indexOf(item)
            newList.removeAt(shimmerPosition)
            newList.add(shimmerPosition, item.copy(updated = true))
            updateData(newList.toList())
        }
    }
}