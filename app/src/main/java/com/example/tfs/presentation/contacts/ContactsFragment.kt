package com.example.tfs.presentation.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.Contact
import com.example.tfs.presentation.profile.ProfileFragment

class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private lateinit var contactRecycler: RecyclerView
    private lateinit var contactListAdapter: ContactViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(view: View) {
        contactRecycler = view.findViewById(R.id.rvContacts)

        contactListAdapter = ContactViewAdapter { item: Contact ->
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ProfileFragment.newInstance(item.userId))
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        contactRecycler.adapter = contactListAdapter

        contactRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }
}