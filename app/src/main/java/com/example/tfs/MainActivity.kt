package com.example.tfs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.data.Reaction
import com.example.tfs.data.TopicCell
import com.example.tfs.ui.emoji.EmojiDialogFragment
import com.example.tfs.ui.topic.TopicAdapterCallback
import com.example.tfs.ui.topic.TopicViewAdapter
import com.example.tfs.util.*


class MainActivity : AppCompatActivity(), TopicAdapterCallback {

    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private lateinit var textMessage: EditText
    private lateinit var sendButton: ImageView

    private var dataSet = TestDataGenerator.generateTestTopic()

    //TODO("remove in future - introduce post_id, pass it to BSD fragment and get back along with emoji code")
    private var currentPost = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSendView()
        onChangeMessage()
        showTopicList(dataSet)

        supportFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val emoji = bundle.getInt(RESULT_KEY, 0)
            when (val post = dataSet[currentPost]) {
                is TopicCell.PostCell -> {
                    updateReaction(emoji, post.reaction)
                    topicListAdapter.submitList(dataSet)
                    topicListAdapter.notifyItemChanged(currentPost)
                }
                is TopicCell.DateCell -> return@setFragmentResultListener
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
            is TopicCell.DateCell -> return //TODO()
        }
    }

    private fun initSendView() {
        textMessage = findViewById(R.id.etMessage)
        sendButton = findViewById(R.id.imgPlus)
    }

    private fun showTopicList(postList: List<TopicCell>) {
        topicRecycler = findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter()
        topicRecycler.adapter = topicListAdapter
        topicListAdapter.setOnCallbackListener(this)

        topicRecycler.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        topicRecycler.visibility = View.VISIBLE
        topicListAdapter.submitList(postList)
    }

    override fun onRecycleViewLongPress(postPosition: Int) {
        currentPost = postPosition
        EmojiDialogFragment().apply {
            show(supportFragmentManager, tag)
        }
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

