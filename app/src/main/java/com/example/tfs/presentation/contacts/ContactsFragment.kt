package com.example.tfs.presentation.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.TopicCell
import com.example.tfs.presentation.topic.TopicViewAdapter
import com.example.tfs.util.TestTopicDataGenerator

class ContactsFragment : Fragment(){

    private lateinit var topicRecycler: RecyclerView
    private lateinit var topicListAdapter: TopicViewAdapter
    private lateinit var textMessage: EditText
    private lateinit var sendButton: ImageView

    //private var dataSet = TestTopicDataGenerator.generateTestTopic()

    //TODO("remove in future - introduce post_id, pass it to BSD fragment and get back along with emoji code")
    private var currentPost = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //showTopicList(view, dataSet)

    }

    private fun initSendView(view: View) {
        textMessage = view.findViewById(R.id.etMessage)
        sendButton = view.findViewById(R.id.imgPlus)
    }

    private fun showTopicList(view: View, postList: List<TopicCell>) {
        topicRecycler = view.findViewById(R.id.rvTopic)
        topicListAdapter = TopicViewAdapter()
        topicRecycler.adapter = topicListAdapter

        topicRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        topicRecycler.visibility = View.VISIBLE
        topicListAdapter.submitList(postList)
    }



    companion object {
        //private const val ARG_MESSAGE = "topic_id"
        fun newInstance(topicId: Int): ContactsFragment {
            val fragment = ContactsFragment()
            val arguments = Bundle()
            //arguments.putInt(ARG_MESSAGE, topicId)
            fragment.arguments = arguments
            return fragment
        }
    }
}