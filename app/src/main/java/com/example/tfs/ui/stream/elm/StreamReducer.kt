package com.example.tfs.ui.stream.elm

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

        is StreamEvent.Internal.LocalLoadingComplete -> {
            if (event.streams.isEmpty()) {
                state { copy(isEmpty = true) }
            } else {
                state { copy(streamListItem = event.streams, isEmpty = false) }
            }
            if (state.isInitial) {
                state { copy(isInitial = false, isLoading = true) }
                commands { +Command.GetRemoteStreams(state.query, state.isSubscribed) }
            } else {
                Any()
            }
        }

        is StreamEvent.Internal.RemoteLoadingComplete -> {
            if (event.streams.isEmpty()) {
                state { copy(isLoading = false, isEmpty = true) }
            } else {
                state { copy(streamListItem = event.streams, isLoading = false, isEmpty = false) }
            }
        }

        is StreamEvent.Internal.LoadingError -> {  //TODO("retry get remote if error from room?")
            state { copy(isLoading = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }

        is StreamEvent.Internal.SearchStreamsComplete -> {
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
            state { copy(query = event.query) }
            if (state.isShowing) {
                state { copy(isLoading = true) }
            }
            commands { +Command.SearchStreams(event.query, state.isSubscribed) }
        }
    }

    override fun Result.ui(event: StreamEvent.Ui) = when (event) {

        is StreamEvent.Ui.Init -> {
            state { copy(isClicked = false, error = null) }
            commands { +Command.ObserveQuery }
            commands { +Command.GetLocalStreams(initialState.query, initialState.isSubscribed) }
        }

        is StreamEvent.Ui.RefreshData -> {
            if (state.isShowing) {
                state { copy(isLoading = true, error = null) }
                commands { +Command.GetRemoteStreams(state.query, state.isSubscribed) }
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
            state { copy(isClicked = true, error = null) }
            commands { +Command.SelectStream(event.streamId) }
        }

        is StreamEvent.Ui.ClickOnTopic -> {
            state { copy(isClicked = false, error = null) }
            effects { +StreamEffect.ShowTopic(event.topicName, event.streamName) }
        }
    }
}