package com.example.tfs.di

import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.domain.streams.StreamRepositoryImpl
import com.example.tfs.domain.topic.FetchTopics
import com.example.tfs.domain.topic.TopicRepositoryImpl
import com.example.tfs.ui.stream.elm.StreamActor
import com.example.tfs.ui.stream.elm.StreamStoreFactory
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerActor
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerStoreFactory
import com.example.tfs.ui.topic.elm.TopicActor
import com.example.tfs.ui.topic.elm.TopicStoreFactory

class AppDI private constructor() {

    private val streamRepository by lazy { StreamRepositoryImpl() }

    private val topicRepository by lazy { TopicRepositoryImpl() }

    private val fetchStreams by lazy { FetchStreams(streamRepository) }

    private val fetchTopics by lazy { FetchTopics(topicRepository) }

    private val streamContainerActor by lazy { StreamContainerActor(fetchStreams) }

    private val streamActor by lazy { StreamActor(fetchStreams) }

    private val topicActor by lazy { TopicActor(fetchTopics) }

    val elmStreamContainerStoreFactory by lazy { StreamContainerStoreFactory(streamContainerActor) }

    val elmStreamStoreFactory by lazy { StreamStoreFactory(streamActor) }

    val elmTopicStoreFactory by lazy { TopicStoreFactory(topicActor)}

    companion object {

        lateinit var INSTANCE: AppDI

        fun init() {
            INSTANCE = AppDI()
        }
    }
}