package com.example.tfs.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.presentation.topic.TopicFragment

class ProfileFragment : Fragment() {

    private var requestContact = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        requestContact = requireArguments().getInt(ARG_MESSAGE, -1)
        return view
    }

    companion object {
        private const val ARG_MESSAGE = "contact_id"
        fun newInstance(contactId: Int): TopicFragment {
            val fragment = TopicFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_MESSAGE, contactId)
            fragment.arguments = arguments
            return fragment
        }
    }
}