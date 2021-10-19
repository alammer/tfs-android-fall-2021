package com.example.tfs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.tfs.customviews.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.ui.emoji.EmojiDialogFragment
import com.example.tfs.ui.topic.TopicAdapterCallback
import com.example.tfs.ui.topic.TopicViewAdapter
import com.example.tfs.util.toast

const val START_CODE_POINT = 0x1f600
const val REQUEST_KEY = "sort_key"
const val RESULT_KEY = "extra_key"

class MainActivity : AppCompatActivity(), TopicAdapterCallback {

    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private val dataSet = generateTestTopic()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendButton = findViewById<ImageView>(R.id.imgPlus)

        showTopicList(dataSet)

//        sendButton.setOnClickListener {
//            EmojiDialogFragment().apply {
//                show(supportFragmentManager, tag)
//            }
//        }
    }

    override fun onRecycleViewItemClick(position: Int, emojiPosition: Int) {
        if (dataSet[position].reaction[emojiPosition].isClicked) {
            dataSet[position].reaction[emojiPosition].count --
            dataSet[position].reaction[emojiPosition].isClicked = false
        } else {
            dataSet[position].reaction[emojiPosition].count ++
            dataSet[position].reaction[emojiPosition].isClicked = true
        }
        topicListAdapter.submitList(dataSet.filter {it.reaction.any {emoji ->  emoji.count == 0 } })
    }

    override fun onRecycleViewLongPress(position: Int) {
        supportFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val selectedEmoji = bundle.getInt(RESULT_KEY, 0)
            dataSet[position].reaction.firstOrNull { it.emoji == selectedEmoji }?.let {
               it.count ++
               it.isClicked = true
            } ?: dataSet[position].reaction.add(Reaction(selectedEmoji, 1, null, true))

            topicListAdapter.submitList(dataSet)
        }
    }

    private fun showTopicList(postList: List<Post>) {
        topicRecycler = findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter()
        topicRecycler.adapter = topicListAdapter
        topicListAdapter.setOnCallbackListener(this)
        topicListAdapter.submitList(postList)

        topicRecycler.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        topicRecycler.visibility = View.VISIBLE

    }
    
    

    private fun generateTestTopic(): MutableList<Post> {
        val testTopic = mutableListOf<Post>()

        (0..(0..20).random()).forEach { _ ->
            testTopic.add(Post(generateTestReaction(), generateTestMessage()))
        }

        return testTopic
    }

    private fun generateTestMessage(): String {
        val testMessage = """
hi if are you new in android use this way Apply your view to make it gone GONE is one way, else, get hold of the parent view, and remove the child from there..... else get the parent layout and use this method an remove all child parentView.remove(child)

I would suggest using the GONE approach...
"""

        return testMessage.substring((1 until (testMessage.length / 2)).random(), (testMessage.length / 2..testMessage.length).random() )
    }

    private fun generateTestReaction(): MutableList<Reaction> {
        val emojiSet = List((0..10).random()) {
            Reaction(
                START_CODE_POINT + (0..40).random(),
                (0..1000).random(),
                null,
                it % 2 == 0
            )
        }
        return emojiSet.toMutableList()
    }

    
}

