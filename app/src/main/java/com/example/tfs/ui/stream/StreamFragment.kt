package com.example.tfs.ui.stream

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.databinding.FragmentStreamBinding
import com.example.tfs.di.DaggerStreamComponent
import com.example.tfs.domain.streams.DomainTopic
import com.example.tfs.ui.stream.adapter.StreamAdapter
import com.example.tfs.ui.stream.adapter.decorations.ItemStreamTypeDecorator
import com.example.tfs.ui.stream.adapter.decorations.ItemTopicTypeDecorator
import com.example.tfs.ui.stream.adapter.items.StreamItem
import com.example.tfs.ui.stream.adapter.items.TopicItem
import com.example.tfs.ui.stream.elm.*
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.toPx
import com.example.tfs.util.viewbinding.viewBinding
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun createStore(): Store<StreamEvent, StreamEffect, StreamState> =
        StreamStore.provide(StreamState(isSubscribed = isSubscribed), streamActor)


    override fun render(state: StreamState) {
        viewBinding.loading.root.isVisible = state.isLoading
        streamAdapter.submitList(state.streamListItem)
    }

    override fun handleEffect(effect: StreamEffect) {
        when (effect) {
            is StreamEffect.LoadingDataError -> {
                with(requireView()) {
                    Log.i("StreamFragment", "Function called: handleEffect() ${effect.error.message}")
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
        val dividerItem = DividerItemDecoration(context,RecyclerView.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.stream_item_divider, null)
            ?.let { drawable -> dividerItem.setDrawable(drawable) }

        with(viewBinding.rvStreams) {
            setHasFixedSize(true)
            adapter = streamAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(ItemStreamTypeDecorator(context, R.layout.item_stream_rv_header, 20.toPx, 40.toPx ))
            addItemDecoration(ItemTopicTypeDecorator(context, R.layout.item_stream_rv_topic, 40.toPx,  4.toPx, 4.toPx))
        }
    }

    private fun getItemTypes() = listOf(
        StreamItem(::clickOnStream),
        TopicItem(::moveToTopic)
    )

    private fun clickOnStream(streamId: Int) {
        store.accept(StreamEvent.Ui.ClickOnStream(streamId))
    }

    private fun moveToTopic(topic: DomainTopic) {
        store.accept(StreamEvent.Ui.ClickOnTopic(topic.name, topic.parentStreamName))
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