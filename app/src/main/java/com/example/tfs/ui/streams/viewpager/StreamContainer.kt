package com.example.tfs.ui.streams.viewpager

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamContainerBinding
import com.example.tfs.ui.streams.StreamViewModel
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator

class StreamContainer : Fragment(R.layout.fragment_stream_container) {

    private val viewBinding by viewBinding(FragmentStreamContainerBinding::bind)
    private val streamViewModel: StreamViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private val viewPagerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when (position) {
                0 -> streamViewModel.showSubscribed(true)
                1 -> streamViewModel.showSubscribed(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.viewPager.unregisterOnPageChangeCallback(viewPagerPageChangeCallback)
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

            viewPager.registerOnPageChangeCallback(viewPagerPageChangeCallback)

            appbar.etSearchInput.doAfterTextChanged {
                streamViewModel.fetchStreams(it.toString())
                if (it.isNullOrBlank()) {
                    appbar.etSearchInput.clearFocus()
                }
            }
        }
    }
}