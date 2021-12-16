package com.example.tfs.ui.stream.adapter.items

import android.view.View
import android.widget.TextView
import com.example.tfs.R
import com.example.tfs.domain.stream.DomainTopic
import com.example.tfs.common.baseadapter.BaseViewHolder

class TopicItemViewHolder(
    private val topicView: View,
    private val onClickTopic: (DomainTopic) -> Unit
) : BaseViewHolder<View, DomainTopic>(topicView) {

    private val topicName: TextView = topicView.findViewById(R.id.tvTopicName)

    override fun onBind(item: DomainTopic) {
        super.onBind(item)
        topicName.text = item.name
        topicView.setOnClickListener { onClickTopic(item) }
    }

    override fun onBind(item: DomainTopic, payloads: List<Any>) {
        super.onBind(item, payloads)
    }
}