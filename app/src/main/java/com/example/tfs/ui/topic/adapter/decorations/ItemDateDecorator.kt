package com.example.tfs.ui.topic.adapter.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ItemDateDecorator(
    private val viewType: Int,
    private val verticalDivider: Int,
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val viewHolder = parent.getChildViewHolder(view)
        if (viewHolder.itemViewType != viewType) return

        with(outRect) {
            top = verticalDivider
            bottom = verticalDivider
        }
    }
}