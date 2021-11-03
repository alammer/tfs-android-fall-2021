package com.example.tfs.ui.streams.viewpager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamContainerBinding
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator

class StreamContainer : Fragment(R.layout.fragment_stream_container) {

    private val viewBinding by viewBinding(FragmentStreamContainerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val pagerAdapter = FragmentPagerAdapter(requireActivity())

        with(viewBinding) {
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(
                appbar.tabLayout,
                viewPager,
            ) { tab, position ->
                val tabNames = listOf("Subscribed", "All streams")
                tab.text = tabNames[position]
            }.attach()
        }
    }
}