package com.example.tfs.ui.streams.viewpager.elm

import vivid.money.elmslie.core.ElmStoreCompat

class StreamStoreFactory(private val actor: StreamActor) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = ViewPagerState(),
            reducer = Reducer(),
            actor = actor
        )
    }

    fun provide() = store
}