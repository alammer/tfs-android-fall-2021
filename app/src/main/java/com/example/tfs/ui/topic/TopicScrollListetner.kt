package com.example.tfs.ui.topic

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class TopicScrollListetner(
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private val linearLayoutManager = layoutManager

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = linearLayoutManager.itemCount
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

        //val updateUpScrollPosition = UPDATE_THRESHOLD
        val updateDownScrollPosition = totalItemCount - UPDATE_THRESHOLD

        if (lastVisibleItemPosition > updateDownScrollPosition && dy > 0 ) {
            loadPage()
        }

        if (firstVisibleItemPosition < UPDATE_THRESHOLD && dy < 0) {
            loadPage(false)
        }
    }

    abstract fun loadPage(isDownScroll: Boolean = true)

    companion object {
        private const val UPDATE_THRESHOLD = 5
    }
}
