package com.example.tfs.ui.topic.adapter

import android.content.res.Resources
import android.util.Log
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.util.rawContent
import com.example.tfs.util.tryToParseContentImage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OwnerPostItemBinder(
    private val onPostTap: (postId: Int, isOwner: Boolean) -> Unit
) {

    fun bind(ownerPostViewHolder: OwnerPostViewHolder, post: PostItem.OwnerPostItem) {

        Single.fromCallable { post.message.tryToParseContentImage(Resources.getSystem()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { ownerPostViewHolder.setMessageText(it) },
                { ownerPostViewHolder.setMessageText(post.message.rawContent()) }
            )

        ownerPostViewHolder.setPostTapListener(post.id, onPostTap)

        ownerPostViewHolder.createPostReaction(post.reaction)
    }
}
