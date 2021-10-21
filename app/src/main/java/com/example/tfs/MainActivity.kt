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
import com.example.tfs.data.Post
import com.example.tfs.data.Reaction
import com.example.tfs.ui.emoji.EmojiDialogFragment
import com.example.tfs.ui.topic.TopicAdapterCallback
import com.example.tfs.ui.topic.TopicViewAdapter

const val EMOJI_START_CODE_POINT = 0x1f600
const val REQUEST_KEY = "emogi_key"
const val RESULT_KEY = "emoji_id"

class MainActivity : AppCompatActivity(), TopicAdapterCallback {

    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private lateinit var textMessage: EditText
    private lateinit var sendButton: ImageView

    private var dataSet = generateTestTopic()
    private var currentPost = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSendView()
        onChangeMessage()
        showTopicList(dataSet)

        supportFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val selectedEmoji = bundle.getInt(RESULT_KEY, 0)
            dataSet[currentPost].reaction.firstOrNull { it.emoji == selectedEmoji }?.apply {
                if (!isClicked) {
                    count += 1
                    isClicked = true
                } else {
                    count -= 1
                    isClicked = false
                    if (count == 0) {
                        dataSet[currentPost].reaction.remove(this)
                    }
                }
            } ?: dataSet[currentPost].reaction.add(Reaction(selectedEmoji, 1, null, true))
            topicListAdapter.submitList(dataSet)
            topicListAdapter.notifyItemChanged(currentPost)
        }

        sendButton.setOnClickListener {
            if (textMessage.text.isNotBlank()) {
                dataSet.add(Post(message = textMessage.text.toString(), isOwner = true))
                topicListAdapter.submitList(dataSet)
                topicListAdapter.notifyItemInserted(dataSet.size)
                textMessage.text.clear()
                sendButton.setImageResource(R.drawable.ic_text_plus)
            }
        }
    }

    override fun onRecycleViewItemClick(position: Int, emojiPosition: Int) {
        dataSet[position].reaction[emojiPosition].apply {
            count = if (isClicked) count - 1 else count + 1
            isClicked = !isClicked
            if (count == 0) {
                dataSet[position].reaction.remove(this)
            }
        }
        topicListAdapter.submitList(dataSet)
        topicListAdapter.notifyItemChanged(position)
    }

    override fun onRecycleViewLongPress(postPosition: Int) {
        currentPost = postPosition
        EmojiDialogFragment().apply {
            show(supportFragmentManager, tag)
        }
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

    private fun initSendView() {
        textMessage = findViewById(R.id.etMessageBody)
        sendButton = findViewById(R.id.imgPlus)
    }

    private fun showTopicList(postList: List<Post>) {
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

    private fun generateTestTopic(): MutableList<Post> {
        val testTopic = mutableListOf<Post>()

        (0..(0..20).random()).forEach {
            testTopic.add(
                Post(
                    generateTestReaction(),
                    generateTestMessage(),
                    isOwner = it % 3 == 0
                )
            )
            testTopic[it].avatar = R.drawable.bad
        }

        testTopic.forEach { post ->
            post.reaction = post.reaction.filter { it.count > 0 }.toMutableList()
        }
        return testTopic
    }

    private fun generateTestMessage(): String {
        val testMessage = """
hi if are you new in android use this way Apply your view to make it gone GONE is one way, else, get hold of the parent view, and remove the child from there..... else get the parent layout and use this method an remove all child parentView.remove(child)

I would suggest using the GONE approach...
"""

        return testMessage.substring(
            (1 until (testMessage.length / 2)).random(),
            (testMessage.length / 2..testMessage.length).random()
        )
    }

    private fun generateTestReaction(): MutableList<Reaction> {
        val emojiSet = List((0..20).random()) {
            Reaction(
                EMOJI_START_CODE_POINT + (0..66).random(),
                (0..100).random(),
                null,
                it % 3 == 0
            )
        }
        return emojiSet.toMutableList()
    }
}

