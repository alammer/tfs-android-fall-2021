package com.example.tfs.ui.stream.streamcontainer.elm


import android.util.Log
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
        /*is Internal.StreamUpdate -> {
            Log.i("StreamContainerReducer", "Function called: internal() ${event.streamId}")
            Any()
        }*/
        is Internal.StreamUpdateComplete -> {
            Log.i("StreamContainerReducer", "Function called: internal()")
            Any()
        }
    }

    override fun Result.ui(event: Ui) = when (event) {

        is Ui.Init -> {
            state { copy(isFetching = true, error = null) }
/*            commands {
                +Command.StreamInteractor(isSubscribed = initialState.isSubscribed,
                    query = initialState.query)
            }*/
            //commands { +Command.UpdateStream }

        }
        is Ui.ChangeSearchQuery -> {
            state {
                copy(isFetching = true,
                    error = null,
                    query = event.query)
            }
            commands { +Command.FetchStreams(state.isSubscribed, state.query) }
        }

        is Ui.FetchRawStreams -> {
            Log.i("ContainerReducer", "Function called: ui()")
            if (state.isSubscribed) {  //ignore duplicate tab clicks
                Log.i("ContainerReducer", "Function called: Any")
                Any()
            } else {
                Log.i("ContainerReducer", "Function called: fetch $state")
                state { copy(isSubscribed = false, isFetching = true, error = null) }
                commands { +Command.FetchStreams(state.isSubscribed, state.query) }
            }
        }

        is Ui.FetchSubscribedStreams -> {
            if (state.isSubscribed) {
                Any()
            } else {
                state { copy(isSubscribed = true, isFetching = true, error = null) }
                commands { +Command.FetchStreams(state.isSubscribed, state.query) }
            }
        }
    }
}