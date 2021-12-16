package com.example.tfs.ui.topic.adapter

import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseAdapter


class TopicAdapter(
    topicItemList: List<AdapterItemBase<*, *>>
) : BaseAdapter(topicItemList) {

/*    override fun addTextShimmerItem(item: AdapterItem, text: String) {
        super.addTextShimmerItem(item, text)

        if (item is DomainStream) {
            val newList = currentData.toMutableList()
            val shimmerPosition = currentData.indexOf(item)
            newList.removeAt(shimmerPosition)
            newList.add(shimmerPosition, item.copy(updated = true))
            newList.add(shimmerPosition + 1, TextShimmer(text))
            updateData(newList.toList())
        }
    }*/
}