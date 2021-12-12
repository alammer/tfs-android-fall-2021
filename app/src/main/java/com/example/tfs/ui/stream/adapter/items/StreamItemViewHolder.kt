package com.example.tfs.ui.stream.adapter.items

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.tfs.R
import com.example.tfs.domain.streams.DomainStream
import com.example.tfs.ui.stream.adapter.base.BaseViewHolder

class StreamItemViewHolder(
    streamView: View,
    private val onClickStream: (Int) -> Unit
) : BaseViewHolder<View, DomainStream>(streamView) {

    private val streamName: TextView = streamView.findViewById(R.id.tvStreamName)
    private val btnTopicList: ImageView = streamView.findViewById(R.id.btnShowTopic)

    override fun onBind(item: DomainStream) {
        super.onBind(item)
        streamName.text = item.name
        btnTopicList.setImageResource(if (item.expanded) R.drawable.ic_collapce else R.drawable.ic_expand)
        btnTopicList.setOnClickListener { onClickStream(item.id) }
    }

    override fun onBind(item: DomainStream, payloads: List<Any>) {
        super.onBind(item, payloads)
    }
}