package com.example.tfs.common.baseadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<out V : View, I : AdapterItem>(
    itemView: V
) : RecyclerView.ViewHolder(itemView) {

    lateinit var item: I

    open fun onBind(item: I) {
        this.item = item
    }

    open fun onBind(item: I, payloads: List<Any>) {
        this.item = item
    }

    open fun onViewDetached() = Unit
}