package com.example.tfs.ui.topic.adapter

import android.content.res.Resources
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.util.rawContent
import com.example.tfs.util.tryToParseContentImage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserPostItemBinder(
    private val onChangeReactionClick: (postId: Int, emojiName: String, emojiCode: String) -> Unit,
    private val onAddReactionClick: (postId: Int) -> Unit,
    private val onPostTap: (postId: Int, isOwner: Boolean) -> Unit,
) {

    fun bind(userPostViewHolder: UserPostViewHolder, post: PostItem.UserPostItem) {

        Single.fromCallable { post.message.tryToParseContentImage(Resources.getSystem()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userPostViewHolder.setMessageText(it) },
                { userPostViewHolder.setMessageText(post.message.rawContent()) }
            )

        userPostViewHolder.setUserName(post.userName)

        userPostViewHolder.setMessageText(post.message)

        userPostViewHolder.setPostTapListener(post.id, onPostTap)

        post.avatar?.let {
            userPostViewHolder.setUserAvatarImage(it)
        } ?: userPostViewHolder.setUserInitilas(post.userName)

        userPostViewHolder.createPostReaction(post.reaction)

        if (post.reaction.isNotEmpty()) {
            userPostViewHolder.addReactionListeners(post.id,
                post.reaction,
                onChangeReactionClick,
                onAddReactionClick,
            )
        }

    }
}
