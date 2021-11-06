package com.example.tfs.ui.topic

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.ui.topic.adapter.TopicViewAdapter
import com.example.tfs.ui.topic.emoji_dialog.EmojiDialogFragment
import com.example.tfs.util.TestMockDataGenerator
import com.example.tfs.util.hideSoftKeyboard
import com.example.tfs.util.toast
import com.example.tfs.util.viewbinding.viewBinding

class TopicFragment : Fragment(R.layout.fragment_topic) {

    private val topicId by lazy {
        requireArguments().getInt(TOPIC_KEY, -1)
    }

    private val streamId by lazy {
        requireArguments().getInt(STREAM_KEY, -1)
    }

    private val viewBinding by viewBinding(FragmentTopicBinding::bind)

    private lateinit var topicListAdapter: TopicViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(TOPIC_REQUEST_KEY, this) { _, bundle ->
            bundle.getIntArray(TOPIC_RESULT_KEY)?.let {
                updateReaction(it[0], it[1])
            }
        }
    }

    private fun initViews() {

        val currentTopic = TestMockDataGenerator.getMockDomainTopic(streamId, topicId)

        if (currentTopic.isEmpty()) {
            requireActivity().toast("Can't find request topic!")
            requireActivity().supportFragmentManager.popBackStack()
        }

        with(viewBinding) {
            tvTopic.text = root.context.getString(
                R.string.topic_name_template,
                requireArguments().getString(TOPIC_NAME, "Unknown")
            )
            tvTopicTitle.text = requireArguments().getString(STREAM_NAME, "Unknown")

            topicListAdapter = TopicViewAdapter(
                { messageId: Int, i: Int -> updateReaction(messageId, i) },
                { messageId -> onRecycleViewLongPress(messageId) }
            )
            rvTopic.adapter = topicListAdapter
            rvTopic.layoutManager = LinearLayoutManager(context)
            topicListAdapter.submitList(currentTopic)

            btnSendPost.setOnClickListener {
                if (etMessage.text.isNotBlank()) {
                    val newTopic = TestMockDataGenerator.addPostToTopic(
                        streamId,
                        topicId,
                        etMessage.text.toString()
                    )
                    topicListAdapter.submitList(newTopic)
                    rvTopic.scrollToPosition(topicListAdapter.itemCount - 1)
                    btnSendPost.setImageResource(R.drawable.ic_text_plus)
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

    private fun updateReaction(messageId: Int, emojiCode: Int) {
        val newTopic = TestMockDataGenerator.updateReaction(streamId, topicId, messageId, emojiCode)
        topicListAdapter.submitList(newTopic)
    }

    companion object {

        private const val STREAM_KEY = "stream_id"
        private const val TOPIC_KEY = "topic_id"
        private const val TOPIC_NAME = "topic_name"
        private const val STREAM_NAME = "stream_name"

        fun newInstance(
            streamId: Int,
            topicId: Int,
            streamName: String,
            topicName: String
        ): TopicFragment {
            return TopicFragment().apply {
                arguments = bundleOf(
                    STREAM_KEY to streamId,
                    TOPIC_KEY to topicId,
                    TOPIC_NAME to topicName,
                    STREAM_NAME to streamName,
                )
            }
        }
    }
}

const val TOPIC_REQUEST_KEY = "emogi_key"
const val TOPIC_RESULT_KEY = "emoji_id"