package com.example.tfs.ui.stream.streamcontainer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tfs.ui.stream.StreamFragment

class FragmentPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StreamFragment.newInstance(true)

            else -> StreamFragment.newInstance(false)
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}