package com.example.tfs

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.ui.ContactListAdapter
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
                1 -> aloneContacMessage(it[0])
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
            btnStart.visibility = View.GONE
            getContacts.launch(null)
        }
    }

    private fun showContactList(contactList: Array<String>){
        contactRecycler = findViewById(R.id.rvContacts)
        contactListAdapter = ContactListAdapter()
        contactRecycler.adapter = contactListAdapter
        contactListAdapter.submitList(contactList.toList())

        contactRecycler.layoutManager =
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(this, GREED_SPAN_COUNT).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }

                else ->
                    LinearLayoutManager(this).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
            }

        contactRecycler.visibility = View.VISIBLE
    }

    private fun aloneContacMessage(aloneContact: String) {
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = aloneContact
    }

    private fun emptyContactsMessage() {
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = "YOUR CONTACT LIST IS EMPTY :("
    }

    private fun errorMessage() {
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = "ERROR FETCHING CONTACTS LIST"
    }
}

private const val GREED_SPAN_COUNT = 2