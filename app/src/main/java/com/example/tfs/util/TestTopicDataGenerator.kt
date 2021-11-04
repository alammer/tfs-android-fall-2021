package com.example.tfs.util

import android.util.Log
import com.example.tfs.domain.*
import com.example.tfs.domain.contacts.Contact
import com.example.tfs.domain.topic.Reaction

private val names = listOf("Ivan", "John", "Petr", "Max", "Mike", "Alex")
private val surnames = listOf("Ivanov", "Smith", "Petrov", "Johnson", "Putin", "Obama")
private val providers = listOf("@mail.ru", "@gmail.com", "@yandex.ru", "@hotmail.com", "@yahoo.com")
private val status = listOf("In a meeting", "Away", "Don't disturb", "Comeback soon", "Very busy", "I'm hungry")
private val expandedStream = mutableListOf<Int>()

object CreateEmojiSet {

    fun createEmojiSet(): List<Int> {
        val emojiSet = mutableListOf<Int>()

        emojiSet.addAll((EMOJI_FACE_START_CODE..EMOJI_FACE_END_CODE))
        emojiSet.addAll((EMOJI_GESTURE_START_CODE..EMOJI_GESTURE_END_CODE))
        emojiSet.addAll((EMOJI_VAR_START_CODE..EMOJI_VAR_END_CODE))

        return emojiSet.toList()
    }
}

object TestMockDataGenerator {

    var subscribed = true

    val mockContactList = getMockContacts()

    private fun getMockContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        (0..(0..50).random()).forEach {
            contacts.add(Contact(
                it,
                names.shuffled().zip(surnames.shuffled()) { name, surname -> "$name $surname" }
                    .first(),
                providers.shuffled().first().run { "mail$it$this" },
                it % 2,
                if (it % 3 == 0) null else status.shuffled().first(),
                null
            ))
        }
        return contacts.toList()
    }

    private val mockStreamList = listOf(
        "general",
        "testing",
        "design",
        "android",
        "iOS",
        "backend",
        "sharing",
        "problems",
        "ML",
        "random"
    )
    private val mockSubcribedList =
        mockStreamList.shuffled().slice(0..(mockStreamList.indices).random())

    private val originalStreamList = getMockRemoteStreams()

    private fun getMockRemoteStreams(): List<RemoteStream> {

        val mockStream = mutableListOf<RemoteStream>()

        mockStreamList.forEachIndexed { index, streamName ->
            mockStream.add(
                RemoteStream(
                    index,
                    streamName,
                    getMockRemoteTopic(streamName, index),
                )
            )
        }
        return mockStream.toList()
    }

    private fun getMockRemoteTopic(streamName: String, parentId: Int): List<RemoteTopic> {
        val testTopic = mutableListOf<RemoteTopic>()

        (1..(1..30).random()).forEach {

            testTopic.add(
                RemoteTopic(
                    parentId * 100 + it,
                    "$streamName-topic$it",
                    parentId,
                    streamName,
                    topic_stat = (0..1000).random(),
                    getMockPostList()
                )
            )
        }
        return testTopic.toList()
    }

    private fun getMockPostList(): List<Post> {
        val testPost = mutableListOf<Post>()
        val startTime = System.currentTimeMillis() - 86400L * 7 * 1000
        (0..(0..30).random()).forEach {
            testPost.add(
                Post(
                    it,
                    it,
                    names.shuffled().zip(surnames.shuffled()) { name, surname -> "$name $surname" }
                        .first(),
                    getMockReaction(),
                    getMockMessage(),
                    null,
                    timeStamp = startTime + (86400 * (it / 3) + 3600 * (it / 2) + 60 * it) * 1000L
                )
            )
        }
        return testPost.toList()
    }

    fun updateReaction(
        streamId: Int,
        topicId: Int,
        messageId: Int,
        emojiCode: Int
    ): List<TopicItem> {
        val currentTopic = originalStreamList
            .firstOrNull { it.streamId == streamId }
            ?.childTopics
            ?.firstOrNull { it.topicId == topicId }
            ?.postList
            ?.toMutableList()
            ?: return emptyList()

        var newReaction = mutableListOf<Reaction>()

        currentTopic.firstOrNull { it.messageId == messageId }?.let {
            Log.i("TestTopicDataGenerator", "Orig: ${it.reaction}")
            newReaction = it.reaction.toMutableList()
            it.reaction.firstOrNull { it.emoji == emojiCode }?.let {
                val index = newReaction.indexOf(it)
                if (it.userId.contains(OWNER_ID)) {
                    //it.userId.remove(OWNER_ID)
                    val newCount = it.count - 1
                    if (newCount == 0) {
                        newReaction.remove(it)
                    } else {
                        newReaction[index] = it.copy(count = it.count - 1)
                    }
                } else {
                    newReaction[index] = it.copy(count = it.count + 1)
                }
            } ?: newReaction.add(
                Reaction(
                    emojiCode,
                    1,
                    listOf(OWNER_ID),
                )
            )
        } ?: return emptyList()

        Log.i("TestTopicDataGenerator", "New: $newReaction")

        currentTopic.firstOrNull { it.messageId == messageId }?.reaction = newReaction

        Log.i("TestTopicDataGenerator", "New: ${currentTopic[0].reaction}")

        val datedPostList = mutableListOf<TopicItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        currentTopic.forEach {
            if (it.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = it.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.shortDate))
                }
            }
            if (it.userName.contains("Alex")) {
                datedPostList.add(it.toDomainOwnerPost())
            } else {
                datedPostList.add(it.toDomainUserPost())
            }
        }
        return datedPostList.toList()
    }

    fun addPostToTopic(streamId: Int, topicId: Int, message: String): List<TopicItem> {
        val currentTopic = originalStreamList
            .firstOrNull { it.streamId == streamId }
            ?.childTopics
            ?.firstOrNull { it.topicId == topicId }
            ?.postList
            ?.toMutableList()
            ?: return emptyList()

        currentTopic.add(
            Post(
                100,
                200,
                "Alex Obama",
                emptyList(),
                message,
                null,
                timeStamp = System.currentTimeMillis()
            )
        )

        val datedPostList = mutableListOf<TopicItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        currentTopic.forEach {
            if (it.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = it.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.shortDate))
                }
            }
            if (it.userName.contains("Alex")) {
                datedPostList.add(it.toDomainOwnerPost())
            } else {
                datedPostList.add(it.toDomainUserPost())
            }
        }
        return datedPostList.toList()
    }

    private fun getMockReaction(): List<Reaction> {
        val reaction = List((0..20).random()) {
            Reaction(
                EMOJI_FACE_START_CODE + (0..66).random(),
                (0..100).random(),
                if(it % 3 == 0) listOf(OWNER_ID) else emptyList(),
            )
        }
        return reaction
    }

    private fun getMockMessage(): String {
        val testMessage = """
       Hi if are you new in android use this way Apply your view to make it gone 
       GONE is one way, else, get hold of the parent view, and remove the child 
       from there..... else get the parent layout and use this method an remove all
        child parentView.remove(child)
       I would suggest using the GONE approach...
       """.trim()

        return testMessage.substring(
            (1 until (testMessage.length / 2)).random(),
            (testMessage.length / 2..testMessage.length).random()
        )
    }

    fun getMockDomainStreamList(): MutableList<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()
        if (subscribed) {
            domainStreamList.addAll(originalStreamList.filter { mockSubcribedList.contains(it.streamName) }
                .map { it.copy().toDomainStream() })
        } else
            domainStreamList.addAll(originalStreamList.map { it.copy().toDomainStream() })
        return domainStreamList
    }

    fun getMockDomainTopic(streamId: Int, topicId: Int): List<TopicItem> {

        val remoteTopic = originalStreamList
            .firstOrNull { it.streamId == streamId }
            ?.childTopics
            ?.firstOrNull { it.topicId == topicId }
            ?: return emptyList()

        val datedPostList = mutableListOf<TopicItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        remoteTopic.postList.forEach {
            if (it.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = it.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.shortDate))
                }
            }

            if (it.userName.contains("Alex")) {
                datedPostList.add(it.toDomainOwnerPost())
            } else {
                datedPostList.add(it.toDomainUserPost())
            }
        }
        return datedPostList
    }


    fun updateStreamMode(streamId: Int = -1): List<StreamItemList> {
        expandedStream.apply {
            if (contains(streamId)) remove(streamId) else add(streamId)
        }

        return updateMockDomainStreamList()
    }

    private fun updateMockDomainStreamList(): List<StreamItemList> {
        val updatedList = mutableListOf<StreamItemList>()

        if (expandedStream.isNotEmpty()) {
            getMockDomainStreamList()
                .map {
                    if ((it is StreamItemList.StreamItem && expandedStream.contains(
                            it.streamId
                        ))
                    ) {
                        updatedList.add(it.copy(expanded = true))
                        updatedList.addAll(it.childTopics)
                    } else updatedList.add(it)
                }
        } else updatedList.addAll(getMockDomainStreamList())

        return updatedList.toList()
    }
}
const val OWNER_ID = 100

const val EMOJI_FACE_START_CODE = 0x1f600
const val EMOJI_FACE_END_CODE = 0x1f644
const val EMOJI_GESTURE_START_CODE = 0x1f645
const val EMOJI_GESTURE_END_CODE = 0x1f64f
const val EMOJI_VAR_START_CODE = 0x1f90c
const val EMOJI_VAR_END_CODE = 0x1f92f








