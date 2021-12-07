package com.example.tfs.ui.stream.elm

import vivid.money.elmslie.core.ElmStoreCompat

object StreamStore  {

    fun provide(state: StreamState, actor: StreamActor) = ElmStoreCompat(
        initialState = state,
        reducer = StreamReducer(),
        actor = actor
    )
}
