package com.example.tfs.ui.topic.elm


import vivid.money.elmslie.core.ElmStoreCompat

object TopicStore {

    fun provide(state: TopicState, actor: TopicActor) = ElmStoreCompat(
        initialState = state,
        reducer = TopicReducer(),
        actor = actor
    )
}