package com.example.tfs.di

import android.content.SharedPreferences
import com.example.tfs.domain.contacts.ContactRepositoryImpl
import com.example.tfs.domain.contacts.FetchContacts
import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.domain.streams.StreamRepositoryImpl
import com.example.tfs.domain.topic.FetchTopics
import com.example.tfs.domain.topic.TopicRepositoryImpl
import com.example.tfs.ui.contacts.elm.ContactActor
import com.example.tfs.ui.contacts.elm.ContactStoreFactory
import com.example.tfs.ui.profile.elm.ProfileActor
import com.example.tfs.ui.profile.elm.ProfileStoreFactory
import com.example.tfs.ui.stream.elm.StreamActor
import com.example.tfs.ui.stream.elm.StreamStoreFactory
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerActor
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerStoreFactory
import com.example.tfs.ui.topic.elm.TopicActor
import com.example.tfs.ui.topic.elm.TopicStoreFactory

class AppDI private constructor(prefs: SharedPreferences) {

    private val streamRepository by lazy { StreamRepositoryImpl() }

    private val fetchStreams by lazy { FetchStreams(streamRepository) }

    private val streamContainerActor by lazy { StreamContainerActor(fetchStreams) }

    private val streamActor by lazy { StreamActor(fetchStreams) }

    private val topicRepository by lazy { TopicRepositoryImpl(prefs) }

    private val fetchTopics by lazy { FetchTopics(topicRepository) }

    private val topicActor by lazy { TopicActor(fetchTopics) }

    private val contactRepository by lazy { ContactRepositoryImpl() }

    private val fetchContacts by lazy { FetchContacts(contactRepository) }

    private val contactActor by lazy { ContactActor(fetchContacts) }

    private val profileActor by lazy { ProfileActor(fetchContacts) }

    val elmStreamContainerStoreFactory by lazy { StreamContainerStoreFactory(streamContainerActor) }

    val elmStreamStoreFactory by lazy { StreamStoreFactory(streamActor) }

    val elmTopicStoreFactory by lazy { TopicStoreFactory(topicActor)}

    val elmContactStoreFactory by lazy { ContactStoreFactory(contactActor) }

    val elmProfileStoreFactory by lazy { ProfileStoreFactory(profileActor) }
    companion object {

        lateinit var INSTANCE: AppDI

        fun init(prefs: SharedPreferences) {
            INSTANCE = AppDI(prefs)
        }
    }
}