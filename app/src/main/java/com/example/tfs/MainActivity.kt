package com.example.tfs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: MaterialButton
    private lateinit var tvContacts: MaterialTextView

    private val getContacts = registerForActivityResult(GetContactsContract()) { data ->
        data?.let {
            when (it.size) {
                0 -> emptyContactsMessage()
                1 -> aloneContacMessage(it[0])
                else -> showContactList(it)
            }
        } ?: errorMessage()
    }

    private fun showContactList(contactList: ArrayList<String>){
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = "YOUR CONTACT LIST FULL OF PERSONS!!!"
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
        tvContacts.text = "CAN'T ACCESS CONTACTS DATA"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MainActivity", "Function called: onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStartActivity2)
        tvContacts = findViewById(R.id.tvEmptyResult)

        btnStart.setOnClickListener {
            btnStart.visibility = View.GONE
            getContacts.launch(null)
        }
    }
}