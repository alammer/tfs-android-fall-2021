package com.example.tfs.ui.stream.elm

import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class StreamReducer :
    ScreenDslReducer<StreamEvent, StreamEvent.Ui, StreamEvent.Internal, StreamState, StreamEffect, Command>(
        StreamEvent.Ui::class,
        StreamEvent.Internal::class
    ) {

    override fun Result.internal(event: StreamEvent.Internal) = when (event) {

        is StreamEvent.Internal.LocalLoadingComplete -> {
            if (state.isInitial) {
                state { copy(isInitial = false, isLoading = true) }
                commands { +Command.UpdateStreamList(state.searchQuery, state.isSubscribed) }
            } else {
                if (event.streams.isEmpty()) {
                    state { copy(isEmpty = true) }
                } else {
                    state { copy(streamListItem = event.streams, isEmpty = false) }
                }
            }
        }

        is StreamEvent.Internal.RemoteLoadingComplete -> {
            if (event.streams.isEmpty()) {
                state { copy(isLoading = false, isEmpty = true) }
            } else {
                state { copy(streamListItem = event.streams, isLoading = false, isEmpty = false) }
            }
        }

        is StreamEvent.Internal.SearchInLocalStreamListComplete -> {
            state { copy(isClicked = false, isLoading = false) }
            if (event.streams.isEmpty()) {
                state { copy(isEmpty = true) }
            } else {
                state { copy(streamListItem = event.streams, isEmpty = false) }
            }
        }

        is StreamEvent.Internal.UpdateStreamComplete -> {
            state { copy(error = null) }
            commands { +Command.SearchInLocalStreamList(state.searchQuery, state.isSubscribed) }
        }

        is StreamEvent.Internal.SearchQueryChange -> {
            state { copy(searchQuery = event.query) }
            if (state.isShowing) {
                state { copy(isLoading = true) }
            }
            commands { +Command.SearchInLocalStreamList(event.query, state.isSubscribed) }
        }

        is StreamEvent.Internal.LoadingError -> {  //TODO("retry get remote if error from room?")
            state { copy(isLoading = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }

        is StreamEvent.Internal.UpdateDataError -> {
            state { copy(isLoading = false, isClicked = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }
    }

    override fun Result.ui(event: StreamEvent.Ui) = when (event) {

        is StreamEvent.Ui.Init -> {
            state { copy(error = null) }
            commands { +Command.ObserveSearchQuery }
            commands { +Command.GetLocalStreamList(initialState.searchQuery, initialState.isSubscribed) }
        }

        is StreamEvent.Ui.RefreshStreamList -> {
            if (state.isShowing) {
                state { copy(isLoading = true, error = null) }
                commands { +Command.UpdateStreamList(state.searchQuery, state.isSubscribed) }
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
            state { copy(error = null) }
            effects { +StreamEffect.ShowTopic(event.topicName, event.streamName) }
        }
    }
}