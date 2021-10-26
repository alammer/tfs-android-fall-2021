package com.example.tfs.presentation.topic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.tfs.data.TopicCell
import com.example.tfs.presentation.topic.emoji.EmojiDialogFragment
import com.example.tfs.ui.topic.TopicViewAdapter
import com.example.tfs.util.TestTopicDataGenerator

class TopicFragment : Fragment(), TopicAdapterCallback {

    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private lateinit var textMessage: EditText
    private lateinit var sendButton: ImageView

    private var dataSet = TestTopicDataGenerator.generateTestTopic()

    //TODO("remove in future - introduce post_id, pass it to BSD fragment and get back along with emoji code")
    private var currentPost = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initSendView(view)
        onChangeMessage()
        showTopicList(view, dataSet)

        this.childFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val emoji = bundle.getInt(RESULT_KEY, 0)
            when (val post = dataSet[currentPost]) {
                is TopicCell.PostCell -> {
                    updateReaction(emoji, post.reaction)
                    topicListAdapter.submitList(dataSet)
                    topicListAdapter.notifyItemChanged(currentPost)
                }
                is TopicCell.LocalDateCell -> return@setFragmentResultListener
            }
        }

        sendButton.setOnClickListener {
            if (textMessage.text.isNotBlank()) {
                dataSet.add(
                    TopicCell.PostCell(
                        message = textMessage.text.toString(),
                        isOwner = true,
                        timeStamp = System.currentTimeMillis()
                    )
                )
                topicListAdapter.submitList(dataSet)
                topicListAdapter.notifyItemInserted(dataSet.size)
                topicRecycler.scrollToPosition(dataSet.size - 1)
                //TODO("HIDE KEYBOARD")
                textMessage.text.clear()
                sendButton.setImageResource(R.drawable.ic_text_plus)
            }
        }
    }

    override fun onRecycleViewItemClick(position: Int, emojiCode: Int) {
        when (val post = dataSet[position]) {
            is TopicCell.PostCell -> {
                updateReaction(emojiCode, post.reaction)
                topicListAdapter.submitList(dataSet)
                topicListAdapter.notifyItemChanged(position)
            }
            is TopicCell.LocalDateCell -> return //TODO()
        }
    }

    private fun initSendView(view: View) {
        textMessage = view.findViewById(R.id.etMessage)
        sendButton = view.findViewById(R.id.imgPlus)
    }

    private fun showTopicList(view: View, postList: List<TopicCell>) {
        topicRecycler = view.findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter()
        topicRecycler.adapter = topicListAdapter
        topicListAdapter.setOnCallbackListener(this)

        topicRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        topicRecycler.visibility = View.VISIBLE
        topicListAdapter.submitList(postList)
    }

    override fun onRecycleViewLongPress(postPosition: Int) {
        currentPost = postPosition
        Log.i("TopicFragment", "Function called: onRecycleViewLongPress()")
        EmojiDialogFragment().show(this.childFragmentManager,tag)
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
}

const val REQUEST_KEY = "emogi_key"
const val RESULT_KEY = "emoji_id"