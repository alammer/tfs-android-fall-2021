package com.example.tfs.ui.streams

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamBinding
import com.example.tfs.domain.streams.StreamItemList
import com.example.tfs.ui.streams.adapter.StreamViewAdapter
import com.example.tfs.ui.streams.viewpager.StreamScreenState
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.util.toast
import com.example.tfs.util.viewbinding.viewBinding

class StreamFragment : Fragment(R.layout.fragment_stream) {

    private val viewBinding by viewBinding(FragmentStreamBinding::bind)

    private val streamViewModel: StreamViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private lateinit var streamViewAdapter: StreamViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        streamViewModel.streamScreenState.observe(viewLifecycleOwner) { processStreamScreenState(it) }
    }

    private fun processStreamScreenState(it: StreamScreenState?) {
        when (it) {
            is StreamScreenState.Result -> {
                streamViewAdapter.submitList(it.items) { viewBinding.rvStreams.scrollToPosition(0) }
                //viewBinding.loadingProgress.isVisible = false
            }
            StreamScreenState.Loading -> {
                // viewBinding.loadingProgress.isVisible = true
            }
            is StreamScreenState.Error -> {
                context.toast(it.error.message)
                streamViewModel.retrySubscribe()
                //viewBinding.loadingProgress.isVisible = false
            }
        }
    }

    private fun initViews() {
        streamViewAdapter = StreamViewAdapter { item: StreamItemList ->
            when (item) {
                is StreamItemList.StreamItem -> clickStreamView(item.name)
                is StreamItemList.TopicItem -> moveToTopicFragment(
                    item.name,
                    item.parentStreamName,
                )
            }
        }

        with(viewBinding) {
            rvStreams.adapter = streamViewAdapter
            rvStreams.layoutManager = LinearLayoutManager(context)

        }
    }

    private fun clickStreamView(streamName: String) {
        streamViewModel.changeStreamMode(streamName)
    }

    private fun moveToTopicFragment(
        topicName: String,
        streamName: String,
    ) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                TopicFragment.newInstance(topicName, streamName)
            )
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    companion object {

        private const val SUBSCRIBED_KEY = "is_subscribed"

        fun newInstance(isSubcribed: Boolean = true): StreamFragment {
            return StreamFragment().apply {
                arguments = bundleOf(
                    SUBSCRIBED_KEY to isSubcribed,
                )
            }
        }
    }
}