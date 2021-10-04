package com.example.tfs

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.model.UserContact
import com.example.tfs.ui.ContactListAdapter
import com.example.tfs.util.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: MaterialButton
    private lateinit var tvContacts: MaterialTextView
    private lateinit var contactRecycler: RecyclerView
    private lateinit var contactListAdapter: ContactListAdapter

    private val getContacts = registerForActivityResult(GetContactsContract()) { data ->
        data?.let {
            when (it.size) {
                0 -> emptyContactsMessage()
                else -> showContactList(it)
            }
        } ?: errorMessage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStartActivity2)
        tvContacts = findViewById(R.id.tvEmptyResult)

        btnStart.setOnClickListener {
            if (hasPermission()) {
                btnStart.visibility = View.GONE
                getContacts.launch(null)
            } else {
                requestPermission()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnStart.visibility = View.GONE
                getContacts.launch(null)
            } else {
                toast("Permission denied..")
            }
        }
    }

    private fun showContactList(contactList: ArrayList<UserContact>){
        contactRecycler = findViewById(R.id.rvContacts)
        contactListAdapter = ContactListAdapter()
        contactRecycler.adapter = contactListAdapter
        contactListAdapter.submitList(contactList)

        contactRecycler.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        contactRecycler.visibility = View.VISIBLE
    }

    private fun emptyContactsMessage() {
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = getString(R.string.empty_contact_message)
    }

    private fun errorMessage() {
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = getString(R.string.error_data_message)
    }
}

