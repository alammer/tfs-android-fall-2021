package com.example.tfs.ui.stream.elm

import android.util.Log
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class StreamReducer :
    ScreenDslReducer<StreamEvent, StreamEvent.Ui, StreamEvent.Internal, StreamState, StreamEffect, Command>(
        StreamEvent.Ui::class,
        StreamEvent.Internal::class
    ) {

    override fun Result.internal(event: StreamEvent.Internal) = when (event) {
        is StreamEvent.Internal.UpdateStreamComplete -> {
            state { copy(isClicked = false) }
            commands { +Command.SearchStreams(state.query, state.isSubscribed) }
        }
        is StreamEvent.Internal.InitialLoadingComplete -> {
            Log.i("StreamReducer", "Function called: Initilal complete ${event.streams.isEmpty()}")
            if (event.streams.isEmpty()) {
                state { copy(isLoading = false, isEmpty = true) }
            } else {
                state { copy(streamListItem = event.streams, isLoading = false, isEmpty = false) }
            }
        }
        is StreamEvent.Internal.InitialLoadingError -> {
            state { copy(isLoading = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }
        is StreamEvent.Internal.UpdateDataComplete -> {
            Log.i("StreamReducer", "Function called: Update complete ${event.streams.isEmpty()}")
            if (event.streams.isEmpty()) {
                state { copy(isLoading = false, isEmpty = true) }
            } else {
                state { copy(streamListItem = event.streams, isLoading = false, isEmpty = false) }
            }
        }
        is StreamEvent.Internal.UpdateDataError -> {
            state { copy(isLoading = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }
        is StreamEvent.Internal.QueryChange -> {
            Log.i("StreamReducer", "Function called: QUERY CHANGE!!!")
            state { copy(query = event.query) }
            if (state.isShowing) {
                state { copy(isLoading = true) }
                commands { +Command.SearchStreams(event.query, state.isSubscribed) }
            } else {
                Any()
            }
        }
    }

    override fun Result.ui(event: StreamEvent.Ui) = when (event) {

        is StreamEvent.Ui.Init -> {
            state { copy(isClicked = false, error = null, isLoading = true) }
            commands { +Command.ObserveQuery }
            commands { +Command.InitilaFetchStreams(initialState.query, initialState.isSubscribed) }
        }

        is StreamEvent.Ui.RefreshData -> {
            if (state.isShowing) {
                state { copy(isLoading = true, error = null) }
                commands { +Command.InitilaFetchStreams(state.query, state.isSubscribed) }
            } else {
                Any()
            }
        }

        is StreamEvent.Ui.ShowFragment -> {
            state { copy(isShowing = true, error = null) }
        }

        is StreamEvent.Ui.HideFragment -> {
            state { copy(isShowing = false, isLoading = false, error = null) }
        }

        is StreamEvent.Ui.ClickOnStream -> {
            state {
                copy(
                    isClicked = true,
                    error = null,
                )
            }
            commands { +Command.SelectStream(event.streamId) }
        }

        is StreamEvent.Ui.ClickOnTopic -> {
            state { copy(isClicked = false, error = null) }
            effects { +StreamEffect.ShowTopic(event.topicName, event.streamName) }
        }
    }
}