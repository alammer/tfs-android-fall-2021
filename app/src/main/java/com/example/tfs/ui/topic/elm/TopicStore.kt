package com.example.tfs.ui.topic.elm


import vivid.money.elmslie.core.ElmStoreCompat

object TopicStore {

    fun provide(actor: TopicActor) = ElmStoreCompat(
        initialState = TopicState(),
        reducer = TopicReducer(),
        actor = actor
    )
}