package com.example.tfs.common.baseitems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder

class LoaderItem : AdapterItemBase<View, BaseLoader> {

    override fun isRelativeItem(item: AdapterItem): Boolean = item is BaseLoader

    override fun getLayoutId() = R.layout.item_loader

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, BaseLoader> {
        val v = layoutInflater.inflate(R.layout.item_loader, parent, false)
        return LoaderItemViewHolder(v)
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<BaseLoader> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<BaseLoader>() {

        override fun areItemsTheSame(oldItem: BaseLoader, newItem: BaseLoader) = true

        override fun areContentsTheSame(oldItem: BaseLoader, newItem: BaseLoader): Boolean = true
    }
}

class LoaderItemViewHolder(
    loaderView: View,
) : BaseViewHolder<View, BaseLoader>(loaderView)

object BaseLoader : AdapterItem