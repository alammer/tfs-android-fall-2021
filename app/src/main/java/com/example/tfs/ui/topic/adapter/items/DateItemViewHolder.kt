package com.example.tfs.ui.topic.adapter.items

import android.view.View
import android.widget.TextView
import com.example.tfs.R
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.topic.DomainPostDate

class DateItemViewHolder(
    datePostView: View,
) : BaseViewHolder<View, DomainPostDate>(datePostView) {

    private val date: TextView = datePostView.findViewById(R.id.tvDateItem)

    override fun onBind(item: DomainPostDate) {
        super.onBind(item)

        date.text = item.postDate
    }

    override fun onBind(item: DomainPostDate, payloads: List<Any>) {
        super.onBind(item, payloads)
    }
}