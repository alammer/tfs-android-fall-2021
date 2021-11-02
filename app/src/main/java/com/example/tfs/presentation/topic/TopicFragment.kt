package com.example.tfs.presentation.topic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.TopicItem
import com.example.tfs.presentation.MainActivity
import com.example.tfs.presentation.topic.emoji.EmojiDialogFragment
import com.example.tfs.util.TestMockDataGenerator
import com.example.tfs.util.toast

class TopicFragment : Fragment(R.layout.fragment_topic) {

    private var topicId = -1
    private var streamId = -1
    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private lateinit var textMessage: EditText
    private lateinit var topicName: TextView
    private lateinit var parentStreamName: TextView
    private lateinit var sendButton: ImageView
    private lateinit var btnTopicNavBack: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNav()

        streamId = requireArguments().getInt(STREAM_KEY, -1)
        topicId = requireArguments().getInt(TOPIC_KEY, -1)

        Log.i("StreamToTopic", "Function called: $streamId $topicId")
        initViews(view)
        onChangeMessage()

        sendButton.setOnClickListener {
            if (textMessage.text.isNotBlank()) {
                val newTopic = TestMockDataGenerator.addPostToTopic(
                    streamId,
                    topicId,
                    textMessage.text.toString()
                )
                topicListAdapter.submitList(newTopic)
                topicRecycler.scrollToPosition(topicListAdapter.itemCount - 1)
                //TODO("HIDE KEYBOARD")
                textMessage.text.clear()
                sendButton.setImageResource(R.drawable.ic_text_plus)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        childFragmentManager.setFragmentResultListener(TOPIC_REQUEST_KEY, this) { _, bundle ->
//            val emoji = bundle.getInt(TOPIC_RESULT_KEY, 0)
//            when (val post = currentTopic[currentPost]) {
//                is TopicItem.UserPostItem -> {
//                    updateReaction(emoji, post.reaction)
//                    topicListAdapter.notifyItemChanged(currentPost)
//                }
//                is TopicItem.LocalDateItem -> return@setFragmentResultListener
//            }
//        }
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showBottomNav()
    }

    private fun initViews(view: View) {

        Log.i("StreamToTopic2", "Function called: $streamId $topicId")
        val currentTopic = TestMockDataGenerator.getMockDomainTopic(streamId, topicId)

        if (currentTopic.isEmpty()) {
            requireActivity().toast("Can't find request topic!")
            requireActivity().supportFragmentManager.popBackStack()
        }

        topicName = view.findViewById(R.id.tvTopic)
        topicName.text = "Topic: ${requireArguments().getString(TOPIC_NAME, "Uknown")}"
        parentStreamName = view.findViewById(R.id.tvTopicTitle)
        parentStreamName.text = "${requireArguments().getString(STREAM_NAME, "Uknown")}"

        textMessage = view.findViewById(R.id.etMessage)
        sendButton = view.findViewById(R.id.imgPlus)
        btnTopicNavBack = view.findViewById(R.id.btnTopicNavBack)

        topicRecycler = view.findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter(
            { topicItem: TopicItem, i: Int -> updateReaction(topicItem, i) },
            { messageId -> onRecycleViewLongPress(messageId) }
        )
        topicRecycler.adapter = topicListAdapter

        topicRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        topicListAdapter.submitList(currentTopic)

        btnTopicNavBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun onRecycleViewLongPress(messageId: Int) {
        EmojiDialogFragment().show(childFragmentManager, tag)
    }

    private fun updateReaction(post: TopicItem, emojiCode: Int) {
//        reaction.firstOrNull { it.emoji == emoji }?.apply {
//            count = if (isClicked) count - 1 else count + 1
//            isClicked = !isClicked
//            if (count == 0) {
//                reaction.remove(this)
//            }
//        } ?: reaction.add(Reaction(emoji, 1, null, true))
    }

    private fun onChangeMessage() {
        textMessage.addTextChangedListener(object : TextWatcher {
            var changeImage = false
            override fun afterTextChanged(p0: Editable?) {
                if (textMessage.text.isNotBlank() && !changeImage) {
                    sendButton.setImageResource(R.drawable.ic_send_arrow)
                    changeImage = true
                }
                if (textMessage.text.isBlank() && changeImage) {
                    sendButton.setImageResource(R.drawable.ic_text_plus)
                    changeImage = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun uknownTopicRequest() {
        requireActivity().apply {
            toast("Topic not exist or removed!")
            supportFragmentManager.popBackStack()
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