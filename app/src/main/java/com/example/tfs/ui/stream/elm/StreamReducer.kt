package com.example.tfs.ui.stream.elm

import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class StreamReducer :
    ScreenDslReducer<StreamEvent, StreamEvent.Ui, StreamEvent.Internal, StreamState, StreamEffect, Command>(
        StreamEvent.Ui::class,
        StreamEvent.Internal::class) {

    override fun Result.internal(event: StreamEvent.Internal) = when (event) {
        is StreamEvent.Internal.UpdateStreamComplete -> {
            state { copy(isClicked = false) }
        }
        is StreamEvent.Internal.LoadingComplete -> {
            state { copy(isLoading = false, streamListItem = event.streams) }
        }

        is StreamEvent.Internal.LoadingError -> {
            state { copy(isLoading = false) }
            effects { +StreamEffect.LoadingDataError(event.error) }
        }
    }

    override fun Result.ui(event: StreamEvent.Ui) = when (event) {

        is StreamEvent.Ui.Init -> {
            state { copy(isLoading = true, isClicked = false, error = null) }
            commands { +Command.ObserveStreams }
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
            state { copy(isClicked = true, error = null) }
            /*commands { +Command.SelectTopic(event.streamId, event.topicId) }*/
        }
    }
}