package com.example.tfs.ui.stream.adapter.items

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.tfs.R
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.stream.DomainStream
import com.facebook.shimmer.ShimmerFrameLayout

class StreamItemViewHolder(
    streamView: View,
    private val onClickStream: (DomainStream) -> Unit
) : BaseViewHolder<View, DomainStream>(streamView) {

    private val streamName: TextView = streamView.findViewById(R.id.tvStreamName)
    private val btnTopicList: ImageView = streamView.findViewById(R.id.btnShowTopic)
    private val shimmerLayout: ShimmerFrameLayout = streamView.findViewById(R.id.bgShimmer)

    override fun onBind(item: DomainStream) {
        super.onBind(item)
        streamName.text = item.name

        if (item.updated.not()) {
            shimmerLayout.visibility = View.GONE
            btnTopicList.visibility = View.VISIBLE
            btnTopicList.setImageResource(if (item.expanded) R.drawable.ic_collapce else R.drawable.ic_expand)
            btnTopicList.setOnClickListener { onClickStream(item) }
        } else {
            shimmerLayout.visibility = View.VISIBLE
            btnTopicList.visibility = View.GONE
        }

    }

    override fun onBind(item: DomainStream, payloads: List<Any>) {
        super.onBind(item, payloads)
    }
}
