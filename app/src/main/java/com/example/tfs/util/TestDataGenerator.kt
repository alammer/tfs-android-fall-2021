package com.example.tfs.util

import com.example.tfs.R
import com.example.tfs.data.Reaction
import com.example.tfs.data.TopicCell
import com.example.tfs.ui.emoji.EMOJI_FACE_START_CODE

object TestDataGenerator {

    fun generateTestTopic(): MutableList<TopicCell> {
        val testTopic = mutableListOf<TopicCell.PostCell>()
        val startTime = System.currentTimeMillis() - 86400L * 500 * 1000

        (0..30).forEach {
            testTopic.add(
                TopicCell.PostCell(
                    generateTestReaction(),
                    generateTestMessage(),
                    isOwner = it % 3 == 0,
                    timeStamp = startTime + (86400 * (it / 3) + 3600 * (it / 2) + 60 * it) * 1000L
                )
            )
            testTopic[it].avatar = R.drawable.bad
        }

        testTopic.forEach { post ->
            post.reaction = post.reaction.filter { it.count > 0 }.toMutableList()
        }

        val datedPostList = mutableListOf<TopicCell>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        testTopic.forEach {
            if (it.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = it.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(TopicCell.DateCell(startTopicDate.fullDate))
                } else {
                    datedPostList.add(TopicCell.DateCell(startTopicDate.shortDate))
                }
            }
            datedPostList.add(it)
        }
        return datedPostList
    }

    private fun generateTestMessage(): String {
        val testMessage = """
hi if are you new in android use this way Apply your view to make it gone GONE is one way, else, get hold of the parent view, and remove the child from there..... else get the parent layout and use this method an remove all child parentView.remove(child)

I would suggest using the GONE approach...
"""

        return testMessage.substring(
            (1 until (testMessage.length / 2)).random(),
            (testMessage.length / 2..testMessage.length).random()
        )
    }

    private fun generateTestReaction(): MutableList<Reaction> {
        val emojiSet = List((0..20).random()) {
            Reaction(
                EMOJI_FACE_START_CODE + (0..66).random(),
                (0..100).random(),
                null,
                it % 3 == 0
            )
        }
        return emojiSet.toMutableList()
    }
}

