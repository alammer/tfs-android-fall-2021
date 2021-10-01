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
        tvContacts.visibility = View.VISIBLE
        tvContacts.text = "TODO"
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