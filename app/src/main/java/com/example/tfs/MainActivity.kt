package com.example.tfs

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tfs.customviews.*
import android.widget.TextView
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val postLayout = findViewById<PostLayout>(R.id.cvPost)
        //val btnTest = findViewById<MaterialButton>(R.id.btnTest)

        //btnTest.setOnClickListener { postLayout.createLayout() }
    }
}

