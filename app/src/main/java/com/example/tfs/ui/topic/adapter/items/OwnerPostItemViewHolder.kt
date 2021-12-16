package com.example.tfs.ui.topic.adapter.items

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
    private val onPostTap: (postId: Int, isOwner: Boolean) -> Unit
) : BaseViewHolder<View, DomainOwnerPost>(ownerPostView) {

    private val postMessage: TextView = ownerPostView.findViewById(R.id.tvPostMessage)
    private val emojiGroup: EmojisLayout = ownerPostView.findViewById(R.id.lEmojis)

    override fun onBind(item: DomainOwnerPost) {
        super.onBind(item)

        postMessage.text = item.message

        ownerPostView.setOnLongClickListener {
            onPostTap(item.id, true)
            return@setOnLongClickListener true
        }

        createPostReaction(item.reaction)
    }

    override fun onBind(item: DomainOwnerPost, payloads: List<Any>) {
        super.onBind(item, payloads)
    }

    private fun createPostReaction(reaction: List<UiItemReaction>) {
        emojiGroup.removeAllViews()
        emojiGroup.addReaction(reaction, true)
    }
}
