package com.example.tfs.util


import com.example.tfs.data.*


const val EMOJI_FACE_START_CODE = 0x1f600
const val EMOJI_FACE_END_CODE = 0x1f644
const val EMOJI_GESTURE_START_CODE = 0x1f645
const val EMOJI_GESTURE_END_CODE = 0x1f64f
const val EMOJI_VAR_START_CODE = 0x1f90c
const val EMOJI_VAR_END_CODE = 0x1f92f

val names = listOf("Ivan", "John", "Petr", "Max", "Mike", "Alex")
val surnames = listOf("Ivanov", "Smith", "Petrov", "Johnson", "Putin", "Obama")

object CreateEmojiSet {

    fun createEmojiSet(): List<Int> {
        val emojiSet = mutableListOf<Int>()

        emojiSet.addAll((EMOJI_FACE_START_CODE..EMOJI_FACE_END_CODE))
        emojiSet.addAll((EMOJI_GESTURE_START_CODE..EMOJI_GESTURE_END_CODE))
        emojiSet.addAll((EMOJI_VAR_START_CODE..EMOJI_VAR_END_CODE))

        return emojiSet.toList()
    }
}


object TestRemoteDataStreamGenerator {

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

    val mockTopicList = List<String>(30) { "topicName $it" }

    private fun getMockReaction(): List<Reaction> {
        val reaction = List((0..20).random()) {
            Reaction(
                EMOJI_FACE_START_CODE + (0..66).random(),
                (0..100).random(),
                null,
                it % 3 == 0
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

    fun getMockRemoteStreams: List<RemoteStream> {

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

        (0..(0..30).random()).forEach {

            testTopic.add(
                RemoteTopic(
                    it,
                    "$streamName-topic$it",
                    parentId,
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
        (0..(0..20).random()).forEach {
            testPost.add(
                Post(
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

    fun getMockDomainTopic(topicId: Int, streamId: Int): MutableList<TopicItem>? {

        val remoteTopic = getMockRemoteStreams()
            .firstOrNull { it.streamId == streamId }
            ?.childTopics
            ?.firstOrNull { it.topicId == topicId }
            ?: return null

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

    fun getMockDomainStreamList(): List<StreamItemList> {
        val remoteStream = getMockRemoteStreams()
        val domainStreamList = mutableListOf<StreamItemList>()
        domainStreamList.addAll(remoteStream.map { it.toDomainStream() })
        return domainStreamList.toList()
    }

    fun updateMockDomainStreamList(currentStreamItemList: MutableList<StreamItemList>, item: StreamItemList.StreamItem): List<StreamItemList> {
        currentStreamItemList
            .asSequence()
            .mapIndexed { index, stream -> if(stream == item ) currentStreamItemList.addAll(index + 1, item.childTopics) }
    }

    fun updateStreamList(currentStreamList: List<StreamItemList>, item: StreamItemList.StreamItem): List<StreamItemList> {
        val domainStreamList = mutableListOf<StreamItemList>()
        domainStreamList.addAll(currentStreamList)
        domainStreamList.firstOrNull { it == item }?.let {
                (it as? StreamItemList.StreamItem)?.expanded = !item.expanded
            }

        returm domainStreamList
            .asSequence()
            .mapIndexed { index, it -> if (it is StreamItemList.StreamItem && it.expanded) domainStreamList.addAll(index + 1, it.childTopics) }.toList()

    }
}








