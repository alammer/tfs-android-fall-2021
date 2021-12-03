package com.example.tfs.ui.topic.elm


import vivid.money.elmslie.core.ElmStoreCompat

class TopicStoreFactory(private val topicActor: TopicActor) {

/*    private val store by lazy {
        ElmStoreCompat(
            initialState = TopicState(),
            reducer = TopicReducer(),
            actor = topicActor
        )
    }*/

    fun provide() =  ElmStoreCompat(
        initialState = TopicState(),
        reducer = TopicReducer(),
        actor = topicActor
    )
}