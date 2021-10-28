package com.example.tfs.presentation.streams

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.StreamCell
import com.example.tfs.presentation.topic.TopicFragment
import com.example.tfs.util.TestStreamDataGenerator
import com.google.android.material.tabs.TabLayout

class StreamsFragment : Fragment() {

    private val streamDataSet: List<StreamCell> =
        TestStreamDataGenerator().generateTestStream()

    private val subscribedStreams = streamDataSet.filterIndexed { index, _ -> index % 3 == 0 }

    private var currentStreamList = mutableListOf<StreamCell>()

    private lateinit var streamListRecycler: RecyclerView
    private lateinit var streamViewAdapter: StreamViewAdapter
    private lateinit var streamTabLayout: TabLayout
    private lateinit var searchInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        streamListRecycler = view.findViewById(R.id.rvStreams)
        streamTabLayout = view.findViewById(R.id.tabLayout)
        searchInput = view.findViewById(R.id.etSearchInput)

        streamViewAdapter = StreamViewAdapter(ItemClickListener { item: StreamCell ->
            when (item) {
                is StreamCell.StreamItemCell -> clickStreamView(item)
                is StreamCell.TopicItemCell -> moveToTopicFragment(item.topicId)
            }
        })
        streamListRecycler.adapter = streamViewAdapter

        streamListRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        currentStreamList = getSubcsribedStreams()

        streamTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    when (position) {
                        0 -> {
                            currentStreamList.clear()
                            currentStreamList = getSubcsribedStreams()
                            streamViewAdapter.submitList(null)
                            streamViewAdapter.submitList(currentStreamList)
                        }
                        1 -> {
                            currentStreamList.clear()
                            currentStreamList.addAll(streamDataSet)
                            streamViewAdapter.submitList(null)
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

    private fun clickStreamView(item: StreamCell) {
        val itemPosition = currentStreamList.indexOf(item)
        var stream = currentStreamList[itemPosition]
        when (stream) {
            is StreamCell.StreamItemCell -> if (stream.expanded) {
                stream.expanded = false
                streamViewAdapter.notifyItemChanged(itemPosition)
                collapseStream(stream)
            } else {
                stream.expanded = true
                streamViewAdapter.notifyItemChanged(itemPosition)
                expandStream(stream)
            }
            else -> return
        }
    }

    private fun expandStream(stream: StreamCell.StreamItemCell) {
        val streamIndex = currentStreamList.indexOf(stream)
        if (streamIndex > -1 && stream.childTopics.isNotEmpty()) {
            currentStreamList.addAll(streamIndex + 1, stream.childTopics)
            streamViewAdapter.notifyItemRangeInserted(streamIndex + 1, stream.childTopics.size )
        }
    }

    private fun collapseStream(stream: StreamCell.StreamItemCell) {
        val streamIndex = currentStreamList.indexOf(stream)
        if (streamIndex > -1 && stream.childTopics.isNotEmpty()) {
            currentStreamList.subList(streamIndex + 1, streamIndex + stream.childTopics.size + 1)
                .clear()
            streamViewAdapter.notifyItemRangeRemoved(streamIndex + 1, stream.childTopics.size )
        }
    }

    private fun moveToTopicFragment(topicId: Int) {
        this.activity?.supportFragmentManager?.beginTransaction()
            ?.add(R.id.fragment_container, TopicFragment.newInstance(topicId))
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun getSubcsribedStreams(): MutableList<StreamCell> {
        val s = mutableListOf<StreamCell>()
        streamDataSet
            .filterIsInstance<StreamCell.StreamItemCell>()
            .filter { subscribedStreams.contains(it) }.map { s.add(it.copy()) }

        return s
    }

}