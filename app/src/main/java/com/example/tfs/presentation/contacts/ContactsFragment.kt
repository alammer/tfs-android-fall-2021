package com.example.tfs.presentation.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.Contact
import com.example.tfs.presentation.profile.ProfileFragment

class ContactsFragment : Fragment() {

    private lateinit var contactRecycler: RecyclerView
    private lateinit var contactListAdapter: ContactViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        contactRecycler = view.findViewById(R.id.rvContacts)

        contactListAdapter = ContactViewAdapter(ItemClickListener { item: Contact ->
            this.activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_container, ProfileFragment.newInstance(item.userId))
                ?.addToBackStack(null)
                ?.commit()

        })

        contactRecycler.adapter = contactListAdapter

        contactRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    companion object {
        fun newInstance(): ContactsFragment {
            val fragment = ContactsFragment()
            val arguments = Bundle()
            //arguments.putInt(ARG_MESSAGE, topicId)
            fragment.arguments = arguments
            return fragment
        }
    }
}