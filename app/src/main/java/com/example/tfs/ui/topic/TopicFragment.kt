package com.example.tfs.ui.topic

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.di.AppDI
import com.example.tfs.ui.topic.adapter.TopicViewAdapter
import com.example.tfs.ui.topic.elm.TopicEffect
import com.example.tfs.ui.topic.elm.TopicEvent
import com.example.tfs.ui.topic.elm.TopicState
import com.example.tfs.ui.topic.emoji_dialog.EmojiDialogFragment
import com.example.tfs.util.hideSoftKeyboard
import com.example.tfs.util.viewbinding.viewBinding
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store


class TopicFragment : ElmFragment<TopicEvent, TopicEffect, TopicState>(R.layout.fragment_topic) {

    override val initEvent: TopicEvent = TopicEvent.Ui.Init

    private val topicName by lazy {
        requireArguments().getString(TOPIC_NAME, "")
    }

    private val streamName by lazy {
        requireArguments().getString(STREAM_NAME, "")
    }

    private val viewBinding by viewBinding(FragmentTopicBinding::bind)

    private lateinit var topicListAdapter: TopicViewAdapter

    override fun createStore(): Store<TopicEvent, TopicEffect, TopicState> =
        AppDI.INSTANCE.elmTopicStoreFactory.provide()


    override fun render(state: TopicState) {
        with(viewBinding) {
            loading.root.isVisible = state.isLoading
            etMessage.setText(state.messageDraft)
            btnSendPost.setImageResource(if (state.messageDraft.isBlank()) R.drawable.ic_text_plus else R.drawable.ic_send_arrow)
            if (state.isNewestPage) rvTopic.scrollToPosition(state.topicList.size - 1) //TODO("create relation with user scroll")
        }
        topicListAdapter.submitList(state.topicList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        store.accept(TopicEvent.Ui.InitialLoad(streamName, topicName))
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
                store.accept(TopicEvent.Ui.NewReactionPicked(updatedMessageId, updatedEmojiCode))
            }
        }
    }

    private fun initViews() {
        with(viewBinding) {
            tvTopic.text = root.context.getString(
                R.string.topic_name_template,
                topicName
            )
            tvStream.text = streamName

            topicListAdapter = TopicViewAdapter(
                { messageId: Int, emojiCode: String ->
                    updateReaction(messageId = messageId,
                        emojiCode = emojiCode)
                },
                { messageId -> addReaction(messageId) }
            )
            rvTopic.adapter = topicListAdapter

            val layoutManager = LinearLayoutManager(context)
            rvTopic.layoutManager = layoutManager

            rvTopic.addOnScrollListener(object : TopicScrollListetner(layoutManager) {
                override fun loadPage(isDownScroll: Boolean) {
                    store.accept(TopicEvent.Ui.PageFetching(isDownScroll))
                }
            })

            btnSendPost.setOnClickListener {
                if (etMessage.text.isNotBlank()) {
                    store.accept(TopicEvent.Ui.MessageSending)
                }
                requireActivity().currentFocus?.apply { hideSoftKeyboard() }
            }

            btnTopicNavBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

            etMessage.doAfterTextChanged {
                store.accept(TopicEvent.Ui.MessageDraftChanging(it.toString()))
            }
        }
    }

    private fun addReaction(messageId: Int) {
        EmojiDialogFragment.newInstance(messageId).show(childFragmentManager, tag)
    }

    private fun updateReaction(messageId: Int, emojiName: String = "", emojiCode: String) {
        store.accept(TopicEvent.Ui.ReactionClicked(messageId, emojiCode))
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