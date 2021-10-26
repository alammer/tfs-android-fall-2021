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
import com.example.tfs.util.TestStreamDataGenerator
import com.google.android.material.tabs.TabLayout

class StreamsFragment : Fragment() {

    private val streamDataSet: MutableList<StreamCell> =
        TestStreamDataGenerator.generateTestStream()

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
                is StreamCell.StreamNameCell -> clickStreamView(item)
                is StreamCell.TopicNameCell -> moveToTopicfragment(item.topicId)
            }
        })

        streamListRecycler.adapter = streamViewAdapter

        streamListRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        Log.i("StreamsFragment", "Function called: initViews() $streamDataSet")
        streamViewAdapter.submitList(streamDataSet)
    }

    private fun clickStreamView(item: StreamCell) {
        Log.i("StreamsFragment", "Get click from ${item.toString()}")

        when (val stream = streamDataSet[streamDataSet.indexOf(item)]) {
            is StreamCell.StreamNameCell -> if (stream.expanded) {
                collapseStream(stream)
                stream.expanded = false
            } else {
                expandStream(stream)
                stream.expanded = true
            }
            else -> return
        }

    }

    private fun expandStream(stream: StreamCell.StreamNameCell) {
        val streamIndex = streamDataSet.indexOf(stream)
        val topicList = stream.childTopics
        if (streamIndex > -1 && topicList.isNotEmpty()) {
            streamDataSet.addAll(streamIndex + 1, stream.childTopics)
            streamViewAdapter.notifyItemRangeInserted(streamIndex + 1, stream.childTopics.size)
        }

    }

    private fun collapseStream(stream: StreamCell.StreamNameCell) {
        val streamIndex = streamDataSet.indexOf(stream)
        val topicList = stream.childTopics
        if (streamIndex > -1 && topicList.isNotEmpty()) {
            streamDataSet.subList(streamIndex + 1, streamIndex + topicList.size).clear()
            streamViewAdapter.notifyItemRangeRemoved(streamIndex + 1, stream.childTopics.size)
        }
    }

    private fun moveToTopicfragment(topicId: Int) {
        Log.i("StreamsFragment", "Get click from ${topicId}")
    }
}