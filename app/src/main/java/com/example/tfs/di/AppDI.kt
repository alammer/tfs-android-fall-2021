package com.example.tfs.di

import android.content.SharedPreferences

class AppDI private constructor(prefs: SharedPreferences) {

    //private val streamRepository by lazy { StreamRepositoryImpl() }
    //private val fetchStreams by lazy { StreamInteractor(streamRepository) }
    //private val topicRepository by lazy { TopicRepositoryImpl(prefs) }
    //private val fetchTopics by lazy { TopicInteractor(topicRepository) }
    //private val contactRepository by lazy { ContactRepositoryImpl() }
    //private val fetchContacts by lazy { ContactInteractor(contactRepository) }

    //private val streamContainerActor by lazy { StreamContainerActor(fetchStreams) }

    //private val streamActor by lazy { StreamActor(fetchStreams) }

    //private val topicActor by lazy { TopicActor(fetchTopics) }

    //private val contactActor by lazy { ContactActor(fetchContacts) }

    //private val profileActor by lazy { ProfileActor(fetchContacts) }

    //val elmStreamContainerStoreFactory by lazy { StreamContainerStore(streamContainerActor) }

    //val elmStreamStoreFactory by lazy { StreamStore(streamActor) }

    //val elmTopicStoreFactory by lazy { TopicStore(topicActor)}

    //val elmContactStoreFactory by lazy { ContactStore(contactActor) }

    //val elmProfileStoreFactory by lazy { ProfileStore(profileActor) }
    companion object {

        lateinit var INSTANCE: AppDI

        fun init(prefs: SharedPreferences) {
            INSTANCE = AppDI(prefs)
        }
    }
}