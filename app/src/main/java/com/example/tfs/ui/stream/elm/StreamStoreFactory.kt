package com.example.tfs.ui.stream.elm

import vivid.money.elmslie.core.ElmStoreCompat

class StreamStoreFactory(private val streamActor: StreamActor) {

/*    private val store by lazy {
        ElmStoreCompat(
            initialState = StreamState(),
            reducer = StreamReducer(),
            actor = streamActor
        )
    }*/

    fun provide() = ElmStoreCompat(
        initialState = StreamState(),
        reducer = StreamReducer(),
        actor = streamActor
    )
}
