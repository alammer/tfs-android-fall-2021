package com.example.tfs.ui.stream.streamcontainer.elm

import vivid.money.elmslie.core.ElmStoreCompat

class StreamContainerStoreFactory(private val containerActor: StreamContainerActor) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = StreamContainerState(),
            reducer = StreamContainerReducer(),
            actor = containerActor
        )
    }

    fun provide() = store
}