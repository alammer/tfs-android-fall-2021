package com.example.tfs.ui.topic.adapter

import android.content.res.Resources
import android.util.Log
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.util.rawContent
import com.example.tfs.util.tryToParseContentImage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserPostItemBinder(
    private val onChangeReactionClick: (Int, Int) -> Unit,
    private val onAddReactionClick: (messageId: Int) -> Unit
) {

    fun bind(userPostViewHolder: UserPostViewHolder, item: PostItem.UserPostItem) {

        Single.fromCallable { item.message.tryToParseContentImage(Resources.getSystem()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userPostViewHolder.setMessageText(it) },
                { userPostViewHolder.setMessageText(item.message.rawContent(Resources.getSystem()))}
            )

        userPostViewHolder.setUserName(item.userName)

        userPostViewHolder.setMessageText(item.message)

        userPostViewHolder.setMessageClickListener(item.id, onAddReactionClick)

        item.avatar?.let {
            userPostViewHolder.setUserAvatarImage(it)
        } ?: userPostViewHolder.setUserInitilas(item.userName)

        if (item.reaction.isNotEmpty()) {
            userPostViewHolder.createPostReaction(item.reaction)
            userPostViewHolder.addReactionListeners(item.id, onChangeReactionClick, onAddReactionClick)
        }

    }
}
