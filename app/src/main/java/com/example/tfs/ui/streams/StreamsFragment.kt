package com.example.tfs.ui.streams

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamsBinding
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.domain.StreamItemList
import com.example.tfs.ui.streams.adapter.StreamViewAdapter
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.util.TestMockDataGenerator
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayout

class StreamsFragment : Fragment(R.layout.fragment_streams) {

    private val viewBinding by viewBinding(FragmentStreamsBinding::bind)

    private lateinit var currentStreamList: List<StreamItemList>
    private lateinit var streamViewAdapter: StreamViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        TestMockDataGenerator.subscribed = true

        streamViewAdapter = StreamViewAdapter { item: StreamItemList ->
            when (item) {
                is StreamItemList.StreamItem -> clickStreamView(item.streamId)
                is StreamItemList.TopicItem -> moveToTopicFragment(
                    item.parentStreamId,
                    item.topicId,
                    item.parentStreamName,
                    item.topicName
                )
                else -> throw IllegalStateException("Unknown viewType")
            }
        }

        with(viewBinding) {

            rvStreams.adapter = streamViewAdapter

            rvStreams.layoutManager = LinearLayoutManager(context)

            currentStreamList = TestMockDataGenerator.getMockDomainStreamList()

            appbar.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.apply {
                        when (position) {
                            0 -> {
                                TestMockDataGenerator.subscribed = true
                                currentStreamList = TestMockDataGenerator.updateStreamMode()
                                streamViewAdapter.submitList(currentStreamList)
                            }
                            1 -> {
                                TestMockDataGenerator.subscribed = false
                                currentStreamList = TestMockDataGenerator.updateStreamMode()
                                streamViewAdapter.submitList(currentStreamList)
                            }
                            else -> throw IllegalArgumentException()
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Handle tab reselect
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Handle tab unselect
                }
            })
        }
        streamViewAdapter.submitList(currentStreamList)
    }

    private fun clickStreamView(streamId: Int) {
        val newList = TestMockDataGenerator.updateStreamMode(streamId)
        streamViewAdapter.submitList(newList)
    }

    private fun moveToTopicFragment(
        parentStreamId: Int,
        topicId: Int,
        streamName: String,
        topicName: String
    ) {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(
                R.id.fragment_container,
                TopicFragment.newInstance(parentStreamId, topicId, streamName, topicName)
            )
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}