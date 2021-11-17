package com.example.tfs.ui.topic

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.ui.topic.adapter.TopicViewAdapter
import com.example.tfs.ui.topic.emoji_dialog.EmojiDialogFragment
import com.example.tfs.util.hideSoftKeyboard
import com.example.tfs.util.toast
import com.example.tfs.util.viewbinding.viewBinding

class TopicFragment : Fragment(R.layout.fragment_topic) {

    private val topicViewModel: TopicViewModel by viewModels()

    private val topicName by lazy {
        requireArguments().getString(TOPIC_NAME, "")
    }

    private val streamName by lazy {
        requireArguments().getString(STREAM_NAME, "")
    }

    private val viewBinding by viewBinding(FragmentTopicBinding::bind)

    private lateinit var topicListAdapter: TopicViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        topicViewModel.topicScreenState.observe(viewLifecycleOwner) {
            processTopicScreenState(it)
        }

        topicViewModel.fetchTopic(streamName to topicName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(TOPIC_REQUEST_KEY, this) { _, bundle ->
            bundle.getBundle(EMOJI_RESPONSE_KEY)?.let { response ->
                val updatedMessageId = response.getInt(EMOJI_RESPONSE_MESSAGE)
                val updatedEmojiName = response.getString(EMOJI_RESPONSE_NAME) ?: return@setFragmentResultListener
                val updatedEmojiCode = response.getString(EMOJI_RESPONSE_CODE) ?: return@setFragmentResultListener
                topicViewModel.addReaction(updatedMessageId, updatedEmojiName, updatedEmojiCode)
            }
        }
    }

    private fun processTopicScreenState(it: TopicScreenState) {
        when (it) {
            is TopicScreenState.Result -> {
                Log.i("TopicFragment", "Function called: processTopicScreenState() ${it.items}")
                topicListAdapter.submitList(it.items) //{ viewBinding.rvTopic.scrollToPosition(0) }
                //viewBinding.loadingProgress.isVisible = false
            }
            TopicScreenState.Loading -> {
                // viewBinding.loadingProgress.isVisible = true
            }
            is TopicScreenState.Error -> {
                context.toast("Error in topic screen: ${it.error.message}")
                Log.i("Repo", "Function called: Topic ${it.error.message}")
                //viewBinding.loadingProgress.isVisible = false
            }
        }
    }

    private fun initViews() {
        with(viewBinding) {
            tvTopic.text = root.context.getString(
                R.string.topic_name_template,
                requireArguments().getString(TOPIC_NAME, "Unknown")
            )
            tvTopicTitle.text = requireArguments().getString(STREAM_NAME, "Unknown")

            topicListAdapter = TopicViewAdapter(
                { messageId: Int, emojiCode: String -> updateReaction(messageId, emojiCode) },
                { messageId -> onRecycleViewLongPress(messageId) }
            )
            rvTopic.adapter = topicListAdapter
            rvTopic.layoutManager = LinearLayoutManager(context)

            btnSendPost.setOnClickListener {
                if (etMessage.text.isNotBlank()) {
                    topicViewModel.sendMessage(streamName, topicName, etMessage.text.toString())
                    rvTopic.scrollToPosition(topicListAdapter.itemCount - 1)
                    btnSendPost.setImageResource(R.drawable.ic_text_plus)
                    //change search query for update topic
                    topicViewModel.fetchTopic(streamName to topicName)
                }
                requireActivity().currentFocus?.apply { hideSoftKeyboard() }
            }

            btnTopicNavBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

            etMessage.doAfterTextChanged {
                if (etMessage.text.isNotBlank()) {
                    btnSendPost.setImageResource(R.drawable.ic_send_arrow)

                }
                if (etMessage.text.isBlank()) {
                    btnSendPost.setImageResource(R.drawable.ic_text_plus)
                }
            }
        }
    }

    private fun onRecycleViewLongPress(messageId: Int) {
        EmojiDialogFragment.newInstance(messageId).show(childFragmentManager, tag)
    }

    private fun updateReaction(messageId: Int, emojiCode: String) {
        topicViewModel.updateReaction(messageId, emojiCode)
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