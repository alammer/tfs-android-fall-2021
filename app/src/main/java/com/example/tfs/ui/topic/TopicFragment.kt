package com.example.tfs.ui.topic

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.common.baseitems.TextShimmerItem
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.di.DaggerTopicComponent
import com.example.tfs.ui.stream.*
import com.example.tfs.ui.topic.adapter.TopicAdapter
import com.example.tfs.ui.topic.adapter.decorations.ItemDateDecorator
import com.example.tfs.ui.topic.adapter.decorations.ItemPostDecorator
import com.example.tfs.ui.topic.adapter.items.DateItem
import com.example.tfs.ui.topic.adapter.items.OwnerPostItem
import com.example.tfs.ui.topic.adapter.items.UserPostItem
import com.example.tfs.ui.topic.elm.*
import com.example.tfs.ui.topic.emoji_dialog.EmojiDialogFragment
import com.example.tfs.util.hideSoftKeyboard
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.toPx
import com.example.tfs.util.viewbinding.viewBinding
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject


class TopicFragment : ElmFragment<TopicEvent, TopicEffect, TopicState>(R.layout.fragment_topic) {

    override val initEvent: TopicEvent = TopicEvent.Ui.Init

    @Inject
    lateinit var topicActor: TopicActor

    private val topicName by lazy {
        requireArguments().getString(TOPIC_NAME, "")
    }

    private val streamName by lazy {
        requireArguments().getString(STREAM_NAME, "")
    }

    private val viewBinding by viewBinding(FragmentTopicBinding::bind)

    private val topicAdapter = TopicAdapter(getItemTypes())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(TOPIC_REQUEST_KEY, this) { _, bundle ->
            bundle.getBundle(EMOJI_RESPONSE_KEY)?.let { response ->
                val updatedMessageId = response.getInt(EMOJI_RESPONSE_MESSAGE)
                val updatedEmojiName =
                    response.getString(EMOJI_RESPONSE_NAME) ?: return@setFragmentResultListener
                val updatedEmojiCode =
                    response.getString(EMOJI_RESPONSE_CODE) ?: return@setFragmentResultListener
                store.accept(
                    TopicEvent.Ui.NewReactionPicked(
                        updatedMessageId,
                        updatedEmojiName,
                        updatedEmojiCode
                    )
                )
            }
        }
    }

    override fun createStore(): Store<TopicEvent, TopicEffect, TopicState> =
        TopicStore.provide(TopicState(topicName = topicName, streamName = streamName), topicActor)


    override fun render(state: TopicState) {
        with(viewBinding) {
            loading.root.isVisible = state.isLoading
        }
        topicAdapter.submitList(state.topicList)
    }

    override fun handleEffect(effect: TopicEffect) {
        when (effect) {
            is TopicEffect.BackNavigation -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
            is TopicEffect.UpdateError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on update topic")
                }
            }
            is TopicEffect.LoadError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load topic")
                }
                //TODO("empty screen)
            }
            is TopicEffect.MessageDraftChange -> {
                viewBinding.btnSendPost.setImageResource(if (effect.draft.isBlank()) R.drawable.ic_text_plus else R.drawable.ic_send_arrow)
            }
            is TopicEffect.MessageSend -> {
                viewBinding.btnSendPost.setImageResource(R.drawable.ic_text_plus)
                viewBinding.etMessage.apply {
                    text.clear()
                    clearFocus()
                }
            }
            is TopicEffect.AddReactionDialog -> {
                EmojiDialogFragment.newInstance(effect.postId).show(childFragmentManager, tag)
            }
            is TopicEffect.LoadTopic -> {
                viewBinding.rvTopic.scrollToPosition(topicAdapter.itemCount - 1)
            }
            is TopicEffect.UpdateTopic -> {
                //TODO("scrolling logic for new page")
            }
            is TopicEffect.NextPageLoad -> {
                //TODO("show PB on bottom RV")
            }
            is TopicEffect.PrevPageLoad -> {
                //TODO("show PB on top RV")
            }
        }
    }

    override fun onAttach(context: Context) {
        DaggerTopicComponent.builder().appComponent(context.appComponent).build()
            .inject(this)
        super.onAttach(context)
    }

    private fun getItemTypes() = listOf(
        OwnerPostItem(::tapOnPost),
        UserPostItem(::updateReaction, ::addReaction, ::tapOnPost),
        DateItem(),
        //LoaderItem(),
        TextShimmerItem()
    )

    private fun initViews() {
        with(viewBinding) {
            tvTopic.text = root.context.getString(
                R.string.topic_name_template,
                topicName
            )
            tvStream.text = streamName

            with(rvTopic) {
                setHasFixedSize(true)

                val adapterLayoutManager = LinearLayoutManager(context)

                topicAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                adapter = topicAdapter

                layoutManager = adapterLayoutManager

                addOnScrollListener(object :
                    TopicScrollListetner(adapterLayoutManager) { //TODO remove in onDestroyView()
                    override fun loadPage(isDownScroll: Boolean) {
                        store.accept(TopicEvent.Ui.PageFetching(isDownScroll))
                    }
                })

                addItemDecoration(
                    ItemDateDecorator(
                        R.layout.item_post_date,
                        DATE_ITEM_DIVIDER.toPx,
                    )
                )

                addItemDecoration(
                    ItemPostDecorator(
                        viewType = R.layout.item_post_owner,
                        verticalDivider = POST_ITEM_DIVIDER.toPx,
                        endPadding = OWNER_POST_ITEM_END_PADDING.toPx
                    )
                )

                addItemDecoration(
                    ItemPostDecorator(
                        viewType = R.layout.item_post,
                        verticalDivider = POST_ITEM_DIVIDER.toPx,
                        startPadding = USER_POST_ITEM_START_PADDING.toPx,
                        endPadding = USER_POST_ITEM_END_PADDING.toPx,
                    )
                )
            }

            btnSendPost.setOnClickListener {
                if (etMessage.text.isNotBlank()) {
                    store.accept(TopicEvent.Ui.MessageSending)
                }
                requireActivity().currentFocus?.apply { hideSoftKeyboard() }
            }

            btnTopicNavBack.setOnClickListener {
                store.accept(TopicEvent.Ui.BackToStream)
            }

            etMessage.doAfterTextChanged {
                store.accept(TopicEvent.Ui.MessageDraftChanging(it.toString()))
            }
        }
    }

    private fun tapOnPost(postId: Int, isOwner: Boolean) {
        Log.i("TopicFragment", "Function called: selectPost() $postId $isOwner")
        //TODO("BSD for selected post")
    }

    private fun addReaction(messageId: Int) {
        store.accept(TopicEvent.Ui.NewReactionAdding(messageId))
    }

    private fun updateReaction(postId: Int, emojiName: String, emojiCode: String) {
        store.accept(TopicEvent.Ui.ReactionClicked(postId, emojiName, emojiCode))
    }

    override fun onDestroyView() {
        viewBinding.rvTopic.clearOnScrollListeners()
        super.onDestroyView()
    }

    companion object {

        private const val TOPIC_NAME = "topic_name"
        private const val STREAM_NAME = "stream_name"

        fun newInstance(
            topicName: String,
            streamName: String,
        ): TopicFragment {
            return TopicFragment().apply {
                arguments = bundleOf(
                    TOPIC_NAME to topicName,
                    STREAM_NAME to streamName,
                )
            }
        }
    }
}

const val TOPIC_REQUEST_KEY = "emoji_request"
const val EMOJI_RESPONSE_KEY = "emoji_response"
const val EMOJI_RESPONSE_MESSAGE = "emoji_key"
const val EMOJI_RESPONSE_NAME = "emoji_name"
const val EMOJI_RESPONSE_CODE = "emoji_id"

private const val DATE_ITEM_DIVIDER = 4
private const val POST_ITEM_DIVIDER = 16
private const val OWNER_POST_ITEM_END_PADDING = 12
private const val USER_POST_ITEM_START_PADDING = 12
private const val USER_POST_ITEM_END_PADDING = 80

