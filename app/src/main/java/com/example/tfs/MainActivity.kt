package com.example.tfs

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.tfs.customviews.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.ui.emoji.EmojiDialogFragment
import com.example.tfs.ui.topic.TopicViewAdapter

const val START_CODE_POINT = 0x1f600

class MainActivity : AppCompatActivity() {

    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendButton = findViewById<ImageView>(R.id.imgPlus)

        showContactList(generateTestTopic())

        sendButton.setOnClickListener {
            EmojiDialogFragment().apply {
                show(supportFragmentManager, tag)
            }
            //topicListAdapter.submitList(generateTestTopic())
        }
    }

    private fun showContactList(contactList: List<Post>) {
        topicRecycler = findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter()
        topicRecycler.adapter = topicListAdapter
        topicListAdapter.submitList(contactList)

        topicRecycler.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        topicRecycler.visibility = View.VISIBLE
    }

    private fun generateTestTopic(): List<Post> {
        val testTopic = mutableListOf<Post>()

        (0..(0..20).random()).forEach { _ ->
            testTopic.add(Post(generateTestReaction(), generateTestMessage()))
        }

        return testTopic.toList()
    }

    private fun generateTestMessage(): String {
        val testMessage = """
hi if are you new in android use this way Apply your view to make it gone GONE is one way, else, get hold of the parent view, and remove the child from there..... else get the parent layout and use this method an remove all child parentView.remove(child)

I would suggest using the GONE approach...
"""

        return testMessage.substring((1 until (testMessage.length / 2)).random(), (testMessage.length / 2..testMessage.length).random() )
    }

    private fun generateTestReaction(): List<Reaction> {
        val emojiSet = List((0..10).random()) {
            Reaction(
                START_CODE_POINT + (0..40).random(),
                (0..1000).random()
            )
        }
        return emojiSet
    }
}

