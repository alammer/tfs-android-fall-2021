package com.example.tfs

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tfs.customviews.EmojiView
import com.example.tfs.customviews.EmojisLayout
import com.example.tfs.customviews.Reaction

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emojisLayout = findViewById<EmojisLayout>(R.id.cvEmoji)

        val dataSet = List<Reaction>(26) { i -> Reaction(('A' + i).toString(), i) }

        emojisLayout.setReactionData(dataSet)
    }


}

