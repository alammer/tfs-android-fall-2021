package com.example.tfs.presentation.streams

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.StreamItemList
import com.example.tfs.presentation.topic.TopicFragment
import com.example.tfs.util.TestMockDataGenerator
import com.google.android.material.tabs.TabLayout

class StreamsFragment : Fragment(R.layout.fragment_streams) {

    private lateinit var currentStreamList: List<StreamItemList>

    private lateinit var streamListRecycler: RecyclerView
    private lateinit var streamViewAdapter: StreamViewAdapter
    private lateinit var streamTabLayout: TabLayout
    private lateinit var searchInput: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        streamListRecycler = view.findViewById(R.id.rvStreams)
        streamTabLayout = view.findViewById(R.id.tabLayout)
        searchInput = view.findViewById(R.id.etSearchInput)

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
            }
        }

        streamListRecycler.adapter = streamViewAdapter

        streamListRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        currentStreamList = TestMockDataGenerator.getMockDomainStreamList()

        streamTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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