package com.example.tfs.ui.topic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentTopicBinding
import com.example.tfs.ui.main.MainActivity
import com.example.tfs.ui.topic.adapter.TopicViewAdapter
import com.example.tfs.ui.topic.emoji_dialog.EmojiDialogFragment
import com.example.tfs.util.TestMockDataGenerator
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
        onChangeMessage()
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
            tvTopic.text = root.context.getString(R.string.topic_name_template, requireArguments().getString(TOPIC_NAME, "Unknown"))
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
                    //TODO("HIDE KEYBOARD")
                    etMessage.text.clear()
                    btnSendPost.setImageResource(R.drawable.ic_text_plus)
                }
            }

            btnTopicNavBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
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

    private fun onChangeMessage() {
        with(viewBinding) {
            etMessage.addTextChangedListener(object : TextWatcher {
                var changeImage = false
                override fun afterTextChanged(p0: Editable?) {
                    if (etMessage.text.isNotBlank() && !changeImage) {
                        btnSendPost.setImageResource(R.drawable.ic_send_arrow)
                        changeImage = true
                    }
                    if (etMessage.text.isBlank() && changeImage) {
                        btnSendPost.setImageResource(R.drawable.ic_text_plus)
                        changeImage = false
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })
        }
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
            val fragment = TopicFragment()
            val arguments = Bundle()
            arguments.putInt(STREAM_KEY, streamId)
            arguments.putInt(TOPIC_KEY, topicId)
            arguments.putString(TOPIC_NAME, topicName)
            arguments.putString(STREAM_NAME, streamName)
            fragment.arguments = arguments
            return fragment
        }
    }
}

const val TOPIC_REQUEST_KEY = "emogi_key"
const val TOPIC_RESULT_KEY = "emoji_id"