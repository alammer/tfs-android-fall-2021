package com.example.tfs.ui.topic.adapter

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.domain.topic.DomainReaction
import com.example.tfs.ui.topic.customview.EmojisLayout
import com.example.tfs.ui.topic.customview.addReaction
import com.example.tfs.util.rawContent
import com.example.tfs.util.tryToParseContentImage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class OwnerPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textMessage = itemView.findViewById<TextView>(R.id.tvPostMessage)
    private val emojiGroup = itemView.findViewById<EmojisLayout>(R.id.lEmojis)

    fun setMessageText(message: CharSequence) {
        textMessage.text = message
    }

    fun createPostReaction(reaction: List<DomainReaction>) {
        emojiGroup.addReaction(reaction, true)
    }
}