package com.example.tfs.ui.stream.adapter.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R


class ItemTopicTypeDecorator(
    private val context: Context,
    private val viewType: Int,
    private val startPadding: Int,
    private val innerDivider: Int,
    private val outerDivider: Int
) : RecyclerView.ItemDecoration() {

    private val colors = listOf(R.color.green_stream, R.color.yellow_stream, R.color.blue_stream)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val viewHolder = parent.getChildViewHolder(view)
        if (viewHolder.itemViewType != viewType) return

        val adapter = parent.adapter ?: return
        val currentPosition =
            parent.getChildAdapterPosition(view).takeIf { it != RecyclerView.NO_POSITION }
                ?: viewHolder.oldPosition

        val isPrevTargetView = adapter.isPrevTargetView(currentPosition, viewType)
        val isNextTargetView = adapter.isNextTargetView(currentPosition, viewType)

        val oneSideInnerDivider = innerDivider / 2

        with(outRect) {
            left = startPadding
            top = if (isPrevTargetView) oneSideInnerDivider else outerDivider
            bottom = if (isNextTargetView) oneSideInnerDivider else outerDivider
        }
    }

    private fun RecyclerView.Adapter<*>.isPrevTargetView(
        currentPosition: Int,
        viewType: Int
    ) = currentPosition != 0 && getItemViewType(currentPosition - 1) == viewType

    private fun RecyclerView.Adapter<*>.isNextTargetView(
        currentPosition: Int,
        viewType: Int
    ): Boolean {
        val lastIndex = itemCount - 1
        return currentPosition != lastIndex && getItemViewType(currentPosition + 1) == viewType
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->

            parent.children
                .forEach { view ->

                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                        .let { if (it == RecyclerView.NO_POSITION) return else it }


                    when (adapter.getItemViewType(childAdapterPosition)) {

                        viewType -> drawBackground(view, canvas, childAdapterPosition)
                        else -> Unit
                    }
                }
        }
    }

    private fun drawBackground(view: View, canvas: Canvas, position: Int) {
        val backPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, colors[position % 3])
        }
        val left = view.left - startPadding
        val top = view.top
        val right = view.right
        val bottom = view.bottom

        canvas.drawRect(Rect(left, top, right, bottom), backPaint)
    }
}

