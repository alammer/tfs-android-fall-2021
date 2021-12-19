package com.example.tfs.ui.contacts

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ContactScrollListener(
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private val linearLayoutManager = layoutManager

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = linearLayoutManager.itemCount

        val updateDownScrollPosition = totalItemCount - UPDATE_THRESHOLD

        if (lastVisibleItemPosition > updateDownScrollPosition && dy > 0) {
            loadPage()
        }
    }

    abstract fun loadPage()
}

private const val UPDATE_THRESHOLD = 5