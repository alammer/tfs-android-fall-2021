package com.example.tfs.ui.topic.adapter

import android.util.Log
import com.example.tfs.domain.topic.PostItem
import com.example.tfs.network.models.Post
import com.example.tfs.network.models.toOwnerPostItem
import com.example.tfs.network.models.toUserPostItem
import com.example.tfs.util.fullDate
import com.example.tfs.util.shortDate
import com.example.tfs.util.startOfDay
import com.example.tfs.util.year
import kotlin.random.Random

internal class TopicToItemMapper : (List<Post>) -> (List<PostItem>) {

    override fun invoke(postList: List<Post>): List<PostItem> = createDomainPostItemList(postList)


    private fun createDomainPostItemList(rawList: List<Post>): List<PostItem> {

        val datedPostList = mutableListOf<PostItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        Log.i("TopicToItemMapper", "Function called: createDomainPostItemList() rawlist $rawList")

        rawList.forEach { post ->
            if (post.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = post.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(PostItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(PostItem.LocalDateItem(startTopicDate.shortDate))
                }
            }

            if (Random.nextBoolean()) {
                datedPostList.add(post.toOwnerPostItem())
            } else {
                datedPostList.add(post.toUserPostItem())
            }
        }
        Log.i("TopicToItemMapper", "Function called: createDomainPostItemList() datedlist $datedPostList")
        return datedPostList.toList()
    }
}