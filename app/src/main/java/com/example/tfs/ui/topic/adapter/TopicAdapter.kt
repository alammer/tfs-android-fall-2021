package com.example.tfs.ui.topic.adapter

import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseAdapter


class TopicAdapter(
    topicItemList: List<AdapterItemBase<*, *>>
) : BaseAdapter(topicItemList) {

    private var isPrevPageLoading = false
    private var isNextPageLoading = false

    override fun addHeaderItem(item: AdapterItem) {
        super.addHeaderItem(item)
        if (isPrevPageLoading.not()) {
            isPrevPageLoading = true
            val newList = currentData.toMutableList()
            newList[0] = item
            updateData(newList.toList())
        }
    }

    override fun addFooterItem(item: AdapterItem) {
        super.addHeaderItem(item)
        if (isNextPageLoading.not()) {
            isNextPageLoading = true
            val newList = currentData.toMutableList()
            newList.add(item)
            updateData(newList.toList())
        }
    }

    fun uploadData(data: List<AdapterItem>) {
        when {
            isPrevPageLoading -> {
                isPrevPageLoading = false
                updateData(data)
            }
            isNextPageLoading -> {
                isNextPageLoading = false
                updateData(data)
            }
            else -> {
                updateData(data)
            }
        }
    }
}