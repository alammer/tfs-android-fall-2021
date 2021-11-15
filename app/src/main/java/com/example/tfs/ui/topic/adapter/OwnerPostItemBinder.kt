package com.example.tfs.ui.topic.adapter

import android.content.res.Resources
import android.util.Log
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.util.rawContent
import com.example.tfs.util.tryToParseContentImage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OwnerPostItemBinder() {

    fun bind(ownerPostViewHolder: OwnerPostViewHolder, item: PostItem.OwnerPostItem) {

        Single.fromCallable { item.message.tryToParseContentImage(Resources.getSystem()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { Log.i("DoOnSuccess", "Function called: $it") }
            .doOnError { Log.i("DoOnError", "Function called: ${it.message}") }
            .subscribe(
                { ownerPostViewHolder.setMessageText(it) },
                { ownerPostViewHolder.setMessageText(item.message.rawContent()) }
            )

        ownerPostViewHolder.createPostReaction(item.reaction)
    }
}
