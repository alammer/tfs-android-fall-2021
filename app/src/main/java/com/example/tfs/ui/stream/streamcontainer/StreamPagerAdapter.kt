package com.example.tfs.ui.stream.streamcontainer

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tfs.ui.stream.StreamFragment

class StreamPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    private var currentSearchQuery: String = ""

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StreamFragment.newInstance(true, currentSearchQuery)

            else -> StreamFragment.newInstance(false, currentSearchQuery)
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    fun setCurrentSearchQuery(query: String) {
        currentSearchQuery = query
    }
}