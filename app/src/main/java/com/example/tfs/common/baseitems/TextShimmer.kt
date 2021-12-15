package com.example.tfs.common.baseitems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder

data class TextShimmer(val text: String) : AdapterItem

class TextShimmerItem() : AdapterItemBase<View, TextShimmer> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is TextShimmer

    override fun getLayoutId() = R.layout.item_text_shimmer

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, TextShimmer> {
        val v = layoutInflater.inflate(R.layout.item_text_shimmer, parent, false)
        return TextShimmerItemViewHolder(v)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<TextShimmer> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<TextShimmer>() {

        override fun areItemsTheSame(oldItem: TextShimmer, newItem: TextShimmer) = true

        override fun areContentsTheSame(oldItem: TextShimmer, newItem: TextShimmer): Boolean = true
    }
}

class TextShimmerItemViewHolder(
    shimmerTextView: View,
) : BaseViewHolder<View, TextShimmer>(shimmerTextView) {

    private val shimmerText: TextView = shimmerTextView.findViewById(R.id.tvShimmerText)

    override fun onBind(item: TextShimmer) {
        super.onBind(item)
        shimmerText.text = item.text
    }
}