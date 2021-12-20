package com.example.tfs.ui.stream

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.common.baseitems.TextShimmerItem
import com.example.tfs.databinding.FragmentStreamBinding
import com.example.tfs.di.DaggerStreamComponent
import com.example.tfs.domain.stream.DomainStream
import com.example.tfs.domain.stream.RelatedTopic
import com.example.tfs.ui.stream.adapter.StreamAdapter
import com.example.tfs.ui.stream.adapter.decorations.ItemDividerDecorator
import com.example.tfs.ui.stream.adapter.decorations.ItemTopicDecorator
import com.example.tfs.ui.stream.adapter.items.StreamItem
import com.example.tfs.ui.stream.adapter.items.TopicItem
import com.example.tfs.ui.stream.elm.*
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.toPx
import com.example.tfs.util.viewbinding.viewBinding
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.storepersisting.retainStoreHolder
import javax.inject.Inject

class StreamFragment :
    ElmFragment<StreamEvent, StreamEffect, StreamState>(R.layout.fragment_stream) {

    override val initEvent: StreamEvent = StreamEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentStreamBinding::bind)

    private val streamAdapter = StreamAdapter(getItemTypes())

    @Inject
    lateinit var streamActor: StreamActor

    private val isSubscribed by lazy {
        requireArguments().getBoolean(SUBSCRIBED_KEY, true)
    }

    private val initialSearchQuery by lazy {
        requireArguments().getString(SEARCH_QUERY_KEY, "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun createStore(): Store<StreamEvent, StreamEffect, StreamState> =
        StreamStore.provide(
            StreamState(isSubscribed = isSubscribed, searchQuery = initialSearchQuery),
            streamActor
        )


    override fun render(state: StreamState) {
        if (state.isShowing) {
            viewBinding.empty.root.isVisible = state.isEmpty
            viewBinding.loading.root.isVisible = state.isLoading
            if (state.isEmpty.not() && state.isClicked.not()) streamAdapter.updateData(state.streamListItem)
        }
    }

    override val storeHolder by retainStoreHolder(storeProvider = ::createStore)

    override fun handleEffect(effect: StreamEffect) {
        when (effect) {
            is StreamEffect.LoadingDataError -> {
                with(requireView()) {
                    Log.i(
                        "StreamFragment",
                        "Function called: handleEffect() ${effect.error.message}"
                    )
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load stream list")
                }
            }

            is StreamEffect.ShowTopic -> {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        TopicFragment.newInstance(effect.topicName, effect.streamName, effect.streamId)
                    )
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun onAttach(context: Context) {
        DaggerStreamComponent.builder()
            .appComponent(context.appComponent)
            .build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        store.accept(StreamEvent.Ui.ShowFragment(isSubscribed))
    }

    override fun onPause() {
        super.onPause()
        //fragment hide by viewpager but continue accept receive search string state from container
        store.accept(StreamEvent.Ui.HideFragment(isSubscribed))
    }


    private fun initViews() {

        with(viewBinding.rvStreams) {
            setHasFixedSize(true)

            streamAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = streamAdapter
            layoutManager = LinearLayoutManager(context)

            itemAnimator = null

            addItemDecoration(
                ItemDividerDecorator(
                    context,
                    R.layout.item_stream,
                )
            )

            addItemDecoration(
                ItemTopicDecorator(
                    context,
                    R.layout.item_related_topic,
                    TOPIC_ITEM_START_PADDING.toPx,
                    TOPIC_ITEM_INNER_DIVIDER.toPx,
                    TOPIC_ITEM_OUTER_DIVIDER.toPx
                )
            )
        }

        viewBinding.swipeLayout.setOnRefreshListener {
            viewBinding.swipeLayout.isRefreshing = false
            store.accept(StreamEvent.Ui.RefreshStreamList)
        }
    }

    private fun getItemTypes() = listOf(
        StreamItem(::clickOnStream),
        TopicItem(::moveToTopic),
    )

    private fun clickOnStream(stream: DomainStream) {
        if (stream.expanded.not() && stream.updated.not()) {
            streamAdapter.addBackgroundShimmer(stream)
        }
        store.accept(StreamEvent.Ui.ClickOnStream(stream.id))
    }

    private fun moveToTopic(topic: RelatedTopic) {
        store.accept(StreamEvent.Ui.ClickOnTopic(topic.name, topic.parentStreamName,topic.parentStreamId))
    }

    companion object {

        private const val SUBSCRIBED_KEY = "is_subscribed"
        private const val SEARCH_QUERY_KEY = "current_query"

        fun newInstance(isSubscribed: Boolean = true, query: String = ""): StreamFragment {
            return StreamFragment().apply {
                arguments = bundleOf(
                    SUBSCRIBED_KEY to isSubscribed,
                    SEARCH_QUERY_KEY to query
                )
            }
        }
    }
}

private const val TOPIC_ITEM_START_PADDING = 40
private const val TOPIC_ITEM_INNER_DIVIDER = 4
private const val TOPIC_ITEM_OUTER_DIVIDER = 4




