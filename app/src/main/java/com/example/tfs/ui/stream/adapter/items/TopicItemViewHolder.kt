package com.example.tfs.ui.stream.adapter.items

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.tfs.R
import com.example.tfs.domain.stream.RelatedTopic
import com.example.tfs.common.baseadapter.BaseViewHolder

class TopicItemViewHolder(
    private val topicView: View,
    private val onClickTopic: (RelatedTopic) -> Unit
) : BaseViewHolder<View, RelatedTopic>(topicView) {

    private val topicName: TextView = topicView.findViewById(R.id.tvTopicName)

    override fun onBind(item: RelatedTopic) {
        super.onBind(item)
        topicName.text = item.name
        topicView.setOnClickListener { onClickTopic(item) }
    }

    override fun onBind(item: RelatedTopic, payloads: List<Any>) {
        super.onBind(item, payloads)
    }
}