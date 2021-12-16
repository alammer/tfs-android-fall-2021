package com.example.tfs.ui.topic.adapter.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemPostDecorator(
    private val viewType: Int,
    private val verticalDivider: Int,
    private val startPadding: Int = 0,
    private val endPadding: Int = 0,
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
            left = startPadding
            right = endPadding
        }
    }
}