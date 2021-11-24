package com.example.tfs.ui.streams.elm

import vivid.money.elmslie.core.ElmStoreCompat

class StreamStoreFactory(private val actor: StreamActor) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = State(),
            reducer = Reducer(),
            actor = actor
        )
    }

    fun provide() = store
}