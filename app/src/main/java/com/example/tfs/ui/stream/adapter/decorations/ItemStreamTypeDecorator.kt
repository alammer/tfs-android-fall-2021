package com.example.tfs.ui.stream.adapter.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R


class ItemStreamTypeDecorator(
    context: Context,
    private val viewType: Int,
    private val startPadding: Int,
    private val endPadding: Int
) :
    RecyclerView.ItemDecoration() {

    private val itemDivider = ContextCompat.getDrawable(context, R.drawable.stream_item_divider)!!

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
            left = startPadding
            right = endPadding
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->

            parent.children
                .forEach { view ->

                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                        .let { if (it == RecyclerView.NO_POSITION) return else it }

                    when (adapter.getItemViewType(childAdapterPosition)) {

                        viewType -> itemDivider.drawSeparator(view, canvas)
                        else -> Unit
                    }
                }
        }
    }

    private fun Drawable.drawSeparator(view: View, canvas: Canvas) =
        apply {
            val left = view.left - startPadding
            val top = view.bottom - intrinsicHeight
            val right = view.right + endPadding
            val bottom = view.bottom
            bounds = Rect(left, top, right, bottom)
            draw(canvas)
        }

}