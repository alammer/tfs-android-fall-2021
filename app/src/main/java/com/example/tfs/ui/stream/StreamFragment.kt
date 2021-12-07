package com.example.tfs.ui.stream

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamBinding
import com.example.tfs.domain.streams.StreamListItem
import com.example.tfs.ui.stream.adapter.StreamViewAdapter
import com.example.tfs.ui.stream.elm.*
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.viewbinding.viewBinding
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class StreamFragment :
    ElmFragment<StreamEvent, StreamEffect, StreamState>(R.layout.fragment_stream) {

    override val initEvent: StreamEvent = StreamEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentStreamBinding::bind)

    private lateinit var streamViewAdapter: StreamViewAdapter

    @Inject
    private lateinit var streamActor: StreamActor

    private val isSubscribed by lazy {
        requireArguments().getBoolean(SUBSCRIBED_KEY, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        store.accept(StreamEvent.Ui.InitialLoad(isSubscribed))
    }

    override fun createStore(): Store<StreamEvent, StreamEffect, StreamState> =
        StreamStore.provide(StreamState(), streamActor)


    override fun render(state: StreamState) {
        streamViewAdapter.submitList(state.streamListItem)
    }

    //override val storeHolder by retainStoreHolder(storeProvider = ::createStore)

    override fun handleEffect(effect: StreamEffect) {
        when (effect) {
            is StreamEffect.LoadingDataError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load stream list")
                }
            }
            is StreamEffect.ShowTopic -> {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        TopicFragment.newInstance(effect.topicName, effect.streamName)
                    )
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun initViews() {
        streamViewAdapter = StreamViewAdapter { item: StreamListItem ->
            when (item) {
                is StreamListItem.StreamItem -> clickOnStream(item.id)
                is StreamListItem.TopicItem -> moveToTopicFragment(
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

    private fun clickOnStream(streamId: Int) {
        store.accept(StreamEvent.Ui.ClickOnStream(streamId))
    }

    private fun moveToTopicFragment(
        topicName: String,
        streamName: String,
    ) {
        store.accept(StreamEvent.Ui.ClickOnTopic(topicName, streamName))
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