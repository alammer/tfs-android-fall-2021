package com.example.tfs.ui.stream.streamcontainer

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tfs.ui.stream.StreamFragment

class FragmentPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    private var currentQuery: String = ""

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StreamFragment.newInstance(true, currentQuery)

            else -> StreamFragment.newInstance(false, currentQuery)
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    fun setCurrentQuery(query: String) {
        currentQuery = query
    }
}