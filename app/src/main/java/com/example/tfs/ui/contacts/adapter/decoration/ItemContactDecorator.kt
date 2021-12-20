package com.example.tfs.ui.contacts.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemContactDecorator(
    private val viewType: Int,
    private val horizontalPadding: Int,
    private val innerDivider: Int = 0,
) : RecyclerView.ItemDecoration() {

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
            top = innerDivider
            bottom = innerDivider
            left = horizontalPadding
            right = horizontalPadding
        }
    }
}