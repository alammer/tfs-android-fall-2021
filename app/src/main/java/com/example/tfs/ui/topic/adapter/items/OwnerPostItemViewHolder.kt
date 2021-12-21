package com.example.tfs.ui.topic.adapter.items

import android.text.Spannable
import android.view.View
import android.widget.TextView
import com.example.tfs.R
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.topic.DomainOwnerPost
import com.example.tfs.domain.topic.UiItemReaction
import com.example.tfs.ui.topic.customview.EmojisLayout
import com.example.tfs.ui.topic.customview.addReaction

class OwnerPostItemViewHolder(
    private val ownerPostView: View,
    private val onPostTap: (post: Int, isOwner: Boolean) -> Unit,
    private val spanFactory: Spannable.Factory
) : BaseViewHolder<View, DomainOwnerPost>(ownerPostView) {

    private val postMessage: TextView = ownerPostView.findViewById(R.id.tvPostMessage)
    private val emojiGroup: EmojisLayout = ownerPostView.findViewById(R.id.lEmojis)

    init {
        ownerPostView.setOnLongClickListener {
            onPostTap(item.id, true)
            return@setOnLongClickListener true
        }

    }

    override fun onBind(item: DomainOwnerPost) {
        super.onBind(item)

        postMessage.setSpannableFactory(spanFactory)

        postMessage.setText(item.content, TextView.BufferType.SPANNABLE)

        createPostReaction(item.reaction)
    }

    private fun createPostReaction(reaction: List<UiItemReaction>) {
        emojiGroup.removeAllViews()
        emojiGroup.addReaction(reaction, true)
    }
}
