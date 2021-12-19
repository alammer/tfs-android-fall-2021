package com.example.tfs.ui.contacts.adapter

import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseAdapter

class ContactAdapter(
    contactItemList: List<AdapterItemBase<*, *>>
) : BaseAdapter(contactItemList) {

/*    fun addBackgroundShimmer(item: AdapterItem) {
        if (item is DomainStream) {
            val newList = currentData.toMutableList()
            val shimmerPosition = currentData.indexOf(item)
            newList.removeAt(shimmerPosition)
            newList.add(shimmerPosition, item.copy(updated = true))
            updateData(newList.toList())
        }
    }*/
}