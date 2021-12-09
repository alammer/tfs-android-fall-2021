package com.example.tfs.ui.stream.streamcontainer.elm


import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerEvent.Internal
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerEvent.Ui
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class StreamContainerReducer :
    ScreenDslReducer<StreamContainerEvent, Ui, Internal, StreamContainerState, StreamContainerEffect, Command>(
        Ui::class,
        Internal::class) {

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.StreamsFetchComplete -> {
            state { copy(isFetching = false) }
        }
        is Internal.StreamsFetchError -> {
            state { copy(isFetching = false) }
            effects { +StreamContainerEffect.FetchError(event.error) }
        }
    }

    override fun Result.ui(event: Ui) = when (event) {

        is Ui.Init -> {
            state { copy(isFetching = true, error = null) }
        }
        is Ui.ChangeSearchQuery -> {
            state {
                copy(isFetching = true,
                    error = null,
                    query = event.query)
            }
            commands { +Command.FetchStreams(state.isSubscribed, state.query) }
        }

        is Ui.ShowRawStreams -> {
            if (state.isSubscribed.not()) {  //ignore duplicate tab clicks
                Any()
            } else {
                state { copy(isSubscribed = false, isFetching = true, error = null) }
                commands { +Command.FetchStreams(state.isSubscribed, state.query) }
            }
        }

        is Ui.ShowSubscribedStreams -> {
            if (state.isSubscribed) {
                Any()
            } else {
                state { copy(isSubscribed = true, isFetching = true, error = null) }
                commands { +Command.FetchStreams(state.isSubscribed, state.query) }
            }
        }
    }
}