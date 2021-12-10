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
        }
        is StreamEvent.Internal.LoadingComplete -> {
            state { copy(streamListItem = event.streams, isLoading = false) }
        }

        is StreamEvent.Internal.LoadingError -> {
            state { copy(isLoading = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }
        is StreamEvent.Internal.QueryChange -> {
            state { copy(query = event.query) }
            if (state.isShowing) {
                state { copy(isLoading = true) }
                commands { +Command.ObserveStreams(event.query, state.isSubscribed) }
            } else {
                Any()
            }
        }
    }

    override fun Result.ui(event: StreamEvent.Ui) = when (event) {

        is StreamEvent.Ui.Init -> {
            state { copy(isClicked = false, error = null) }
            commands {
                +Command.ObserveQuery
            }
        }

        is StreamEvent.Ui.ShowFragment -> {
            state { copy(isShowing = true, /*isLoading = true,*/ error = null) }
            commands { +Command.ObserveStreams(state.query, state.isSubscribed) }
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