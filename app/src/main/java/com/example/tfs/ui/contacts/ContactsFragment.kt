package com.example.tfs.ui.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.databinding.FragmentContactsBinding
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.domain.contacts.Contact
import com.example.tfs.ui.contacts.adapter.ContactViewAdapter
import com.example.tfs.ui.profile.ProfileFragment
import com.example.tfs.util.TestMockDataGenerator
import com.example.tfs.util.viewbinding.viewBinding

class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private val viewBinding by viewBinding(FragmentContactsBinding::bind)
    private lateinit var contactListAdapter: ContactViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        contactListAdapter = ContactViewAdapter { item: Contact ->
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ProfileFragment.newInstance(item.userId))
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        val contactList = TestMockDataGenerator.mockContactList

        with(viewBinding) {
            rvContacts.adapter = contactListAdapter

            rvContacts.layoutManager = LinearLayoutManager(context)
        }

        contactListAdapter.submitList(contactList)
    }
}