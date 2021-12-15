package com.example.tfs.common.baseadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil

interface AdapterItemBase<VH : View, Item : AdapterItem> {

    fun isRelativeItem(item: AdapterItem): Boolean

    @LayoutRes
    fun getLayoutId(): Int

    fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<VH, Item>

    fun getDiffUtil(): DiffUtil.ItemCallback<Item>

}