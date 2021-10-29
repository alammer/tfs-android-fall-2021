package com.example.tfs.presentation.topic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.Reaction
import com.example.tfs.data.TopicItem
import com.example.tfs.presentation.MainActivity
import com.example.tfs.presentation.streams.StreamsFragment
import com.example.tfs.presentation.topic.emoji.EmojiDialogFragment
import com.example.tfs.util.TestTopicDataGenerator

class TopicFragment : Fragment(), TopicAdapterCallback {

    private var requestTopic = -1
    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private lateinit var textMessage: EditText
    private lateinit var sendButton: ImageView
    private lateinit var btnTopicNavBack: ImageView

    private var dataSet = TestTopicDataGenerator.generateTestTopic()

    //TODO("remove in future - introduce post_id, pass it to BSD fragment and get back along with emoji code")
    private var currentPost = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic, container, false)
        requestTopic = requireArguments().getInt(ARG_MESSAGE, -1)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNav()
        initViews(view)
        onChangeMessage()dataSet.getOrNull(requestTopic)?.let {
           showTopicList(view, it)
        } ?: TODO("make mock list with topics")

        this.childFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val emoji = bundle.getInt(RESULT_KEY, 0)
            when (val post = dataSet[currentPost]) {
                is TopicItem.PostItem -> {
                    updateReaction(emoji, post.reaction)
                    topicListAdapter.notifyItemChanged(currentPost)
                }
                is TopicItem.LocalDateItem -> return@setFragmentResultListener
            }
        }

        sendButton.setOnClickListener {
            if (textMessage.text.isNotBlank()) {
                dataSet.add(
                    TopicItem.PostItem(
                        message = textMessage.text.toString(),
                        isOwner = true,
                        timeStamp = System.currentTimeMillis()
                    )
                )
                topicListAdapter.notifyItemInserted(dataSet.size)
                topicRecycler.scrollToPosition(dataSet.size - 1)
                //TODO("HIDE KEYBOARD")
                textMessage.text.clear()
                sendButton.setImageResource(R.drawable.ic_text_plus)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showBottomNav()
    }

    override fun onRecycleViewItemClick(position: Int, emojiCode: Int) {
        when (val post = dataSet[position]) {
            is TopicItem.PostItem -> {
                updateReaction(emojiCode, post.reaction)
                topicListAdapter.notifyItemChanged(position)
            }
            is TopicItem.LocalDateItem -> return //TODO()
        }
    }

    private fun initViews(view: View) {
        textMessage = view.findViewById(R.id.etMessage)
        sendButton = view.findViewById(R.id.imgPlus)
        btnTopicNavBack = view.findViewById(R.id.btnTopicNavBack)

        topicRecycler = view.findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter()
        topicRecycler.adapter = topicListAdapter
        topicListAdapter.setOnCallbackListener(this)

        topicRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        topicRecycler.visibility = View.VISIBLE
        topicListAdapter.submitList(dataSet)

        btnTopicNavBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onRecycleViewLongPress(postPosition: Int) {
        currentPost = postPosition
        EmojiDialogFragment().show(this.childFragmentManager, tag)
    }

    private fun updateReaction(emoji: Int, reaction: MutableList<Reaction>) {
        reaction.firstOrNull { it.emoji == emoji }?.apply {
            count = if (isClicked) count - 1 else count + 1
            isClicked = !isClicked
            if (count == 0) {
                reaction.remove(this)
            }
        } ?: reaction.add(Reaction(emoji, 1, null, true))
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

    companion object {
        private const val ARG_MESSAGE = "topic_id"
        fun newInstance(topicId: Int): TopicFragment {
            val fragment = TopicFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_MESSAGE, topicId)
            fragment.arguments = arguments
            return fragment
        }
    }
}

const val REQUEST_KEY = "emogi_key"
const val RESULT_KEY = "emoji_id"