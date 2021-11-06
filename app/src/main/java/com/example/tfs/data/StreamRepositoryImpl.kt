package com.example.tfs.data

import com.example.tfs.domain.contacts.Contact
import com.example.tfs.domain.streams.*
import com.example.tfs.domain.topic.Reaction
import com.example.tfs.domain.topic.TopicItem
import com.example.tfs.util.*
import kotlin.random.Random

private val names = listOf("Ivan", "John", "Petr", "Max", "Mike", "Alex")
private val surnames = listOf("Ivanov", "Smith", "Petrov", "Johnson", "Putin", "Obama")
private val providers = listOf("@mail.ru", "@gmail.com", "@yandex.ru", "@hotmail.com", "@yahoo.com")
private val status =
    listOf("In a meeting", "Away", "Don't disturb", "Comeback soon", "Very busy", "I'm hungry")
private val mockStreamList =
    listOf(
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

class StreamRepositoryImpl {
    var subscribed = true

    private val contactNames = names.shuffled()
        .zip(surnames.shuffled()) { name, surname -> "$name $surname" }

    private val contactList = getMockContacts()

    private val mockSubcribedList =
        mockStreamList.shuffled().slice(0..(mockStreamList.indices).random())

    private val remoteStreamList = getMockRemoteStreams()

    private val remoteTopicList = getMockRemoteTopicList()

    private fun getMockRemoteStreams(): List<RemoteStream> {
        val mockStream = mutableListOf<RemoteStream>()

        mockStreamList.forEachIndexed { index, streamName ->
            mockStream.add(
                RemoteStream(
                    id = index,
                    name = streamName,
                    streamTraffic = (0..1000).random()
                )
            )
        }
        return mockStream.toList()
    }

    private fun getMockRemoteTopicList(): List<RemoteTopic> {
        val testTopic = mutableListOf<RemoteTopic>()

        (1..(1..30).random()).forEach {
            testTopic.add(
                RemoteTopic(
                    name = "topic-$it",
                    parentStreamName = mockStreamList.shuffled().first(),
                    topic_stat = (0..1000).random(),
                    getMockPostList()
                )
            )
        }
        return testTopic.toList()
    }

    private fun getStreamRelatedTopicList(streamName: String) = remoteTopicList.filter { it.parentStreamName == streamName }

    //тут добавляем списки топиков к темам
    fun getMockDomainStreamList(subscribed: Boolean = false, expandedStreams: List<String>): MutableList<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()
        val streamList = if (subscribed) {
            remoteStreamList.filter { mockSubcribedList.contains(it.name) }
        } else {
            remoteStreamList
        }
        if (subscribed) {
            domainStreamList.addAll(remoteStreamList.filter { mockSubcribedList.contains(it.name) }
                .map { it.toDomainStream() })
        } else
            domainStreamList.addAll(remoteStreamList.map { it.toDomainStream() })
        return domainStreamList
    }



    fun getMockDomainTopic(streamName: String, topicName: String): List<TopicItem> {
        val topic = remoteTopicList.first { it.parentStreamName == streamName && it.name == topicName }
        val datedPostList = mutableListOf<TopicItem>()
        var startTopicDate = 0L
        val currentDate = System.currentTimeMillis()

        topic.postList.forEach {
            if (it.timeStamp.startOfDay() > startTopicDate) {
                startTopicDate = it.timeStamp.startOfDay()
                if (startTopicDate.year < currentDate.year) {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.fullDate))
                } else {
                    datedPostList.add(TopicItem.LocalDateItem(startTopicDate.shortDate))
                }
            }

            if (it.isOwner) {
                datedPostList.add(it.toDomainOwnerPost())
            } else {
                datedPostList.add(it.toDomainUserPost())
            }
        }
        return datedPostList
    }

    private fun getMockPostList(): List<Post> {
        val testPost = mutableListOf<Post>()
        val startTime = System.currentTimeMillis() - 86400L * 7 * 1000
        (0..(0..30).random()).forEach {
            val sender = contactList.shuffled().first()
            testPost.add(
                Post(
                    id = it,
                    isOwner = (it % 5 == 0),
                    senderId = sender.id,
                    senderName = sender.name,
                    reaction = getMockReaction(),
                    content = getMockMessage(),
                    timeStamp = startTime + (86400 * (it / 3) + 3600 * (it / 2) + 60 * it) * 1000L
                )
            )
        }
        return testPost.toList()
    }

    private fun getMockContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        (0..(0..50).random()).forEach {
            contacts.add(
                Contact(
                    it,
                    contactNames.shuffled().first(),
                    providers.shuffled().first().run { "mail$it$this" },
                    it % 2,
                    Random.nextBoolean(),
                    null,
                    null
                )
            )
        }
        return contacts.toList()
    }

    private fun getMockReaction(): List<Reaction> {
        val reaction = List((0..20).random()) {
            Reaction(
                (EMOJI_FACE_START_CODE + it).toString(),
                "emoji",
                contactList.shuffled().first().id,
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
}