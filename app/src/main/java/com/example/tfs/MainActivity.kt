package com.example.tfs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    private val startButton = findViewById<MaterialButton>(R.id.btnStartActivity2)
    private val tvContacts = findViewById<MaterialTextView>(R.id.tvEmptyResult)

    private val getContacts = registerForActivityResult(GetContactsContract()) { text ->
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startButton.setOnClickListener {
            startButton.visibility = View.GONE
            getContacts.launch(null)
        }
    }
}