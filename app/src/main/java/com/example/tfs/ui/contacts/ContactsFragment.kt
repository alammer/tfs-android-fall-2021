package com.example.tfs.ui.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentContactsBinding
import com.example.tfs.ui.contacts.adapter.ContactViewAdapter
import com.example.tfs.ui.profile.ProfileFragment
import com.example.tfs.util.viewbinding.viewBinding

class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private val contactsViewModel: ContactsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentContactsBinding::bind)
    private lateinit var contactListAdapter: ContactViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        contactsViewModel.contactList.observe(viewLifecycleOwner, {
            contactListAdapter.submitList(it)
        })

    }

    private fun initViews() {
        contactListAdapter = ContactViewAdapter { contactId: Int ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment.newInstance(contactId))
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        with(viewBinding) {
            rvContacts.adapter = contactListAdapter
            rvContacts.layoutManager = LinearLayoutManager(context)
        }
    }
}