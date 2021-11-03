package com.example.tfs.ui.streams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamBinding
import com.example.tfs.domain.StreamItemList
import com.example.tfs.ui.streams.adapter.StreamViewAdapter
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.util.TestMockDataGenerator
import com.example.tfs.util.viewbinding.viewBinding

class StreamFragment : Fragment(R.layout.fragment_stream) {

    private val viewBinding by viewBinding(FragmentStreamBinding::bind)

    private lateinit var currentStreamList: List<StreamItemList>
    private lateinit var streamViewAdapter: StreamViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

        TestMockDataGenerator.subscribed = requireArguments().getBoolean(SUBSCRIBED_KEY, true)

        currentStreamList = TestMockDataGenerator.getMockDomainStreamList()
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

    companion object {
        private const val SUBSCRIBED_KEY = "is_sucsribed"
        fun newInstance(isSubcribed: Boolean = true): StreamFragment {
            val fragment = StreamFragment()
            val arguments = Bundle()
            arguments.putBoolean(SUBSCRIBED_KEY, isSubcribed)
            fragment.arguments = arguments
            return fragment
        }
    }
}