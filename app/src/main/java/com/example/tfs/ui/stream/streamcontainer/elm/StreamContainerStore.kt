package com.example.tfs.ui.stream.streamcontainer.elm

import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

object StreamContainerStore {

    fun provide(actor: StreamContainerActor) = ElmStoreCompat(
        initialState = StreamContainerState(),
        reducer = StreamContainerReducer(),
        actor = actor
    )
}