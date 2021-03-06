package com.example.tfs.ui.topic.adapter.items

import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.tfs.R
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.common.baseadapter.AdapterItemBase
import com.example.tfs.common.baseadapter.BaseViewHolder
import com.example.tfs.domain.topic.DomainUserPost

class UserPostItem(
    private val onChangeReactionClick: (postId: Int, emojiName: String, emojiCode: String) -> Unit,
    private val onAddReactionClick: (postId: Int) -> Unit,
    private val onPostTap: (postId: Int, isOwner: Boolean) -> Unit,
) : AdapterItemBase<View, DomainUserPost> {

    private val spannableFactory = object : Spannable.Factory() {
        override fun newSpannable(source: CharSequence?): Spannable {
            return source as Spannable
        }
    }

    override fun isRelativeItem(item: AdapterItem): Boolean = item is DomainUserPost

    override fun getLayoutId() = R.layout.item_post

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<View, DomainUserPost> {
        val v = layoutInflater.inflate(R.layout.item_post, parent, false)
        return UserPostItemViewHolder(
            v,
            onChangeReactionClick,
            onAddReactionClick,
            onPostTap,
            spannableFactory
        )
    }

    override fun getDiffUtil(): DiffUtil.ItemCallback<DomainUserPost> = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DomainUserPost>() {

        override fun areItemsTheSame(oldItem: DomainUserPost, newItem: DomainUserPost) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DomainUserPost, newItem: DomainUserPost) =
            oldItem.message == newItem.message && oldItem.reaction == newItem.reaction

        override fun getChangePayload(oldItem: DomainUserPost, newItem: DomainUserPost): Any? {
            if (oldItem.reaction != newItem.reaction) return newItem.reaction
            return super.getChangePayload(oldItem, newItem)
        }
    }
}