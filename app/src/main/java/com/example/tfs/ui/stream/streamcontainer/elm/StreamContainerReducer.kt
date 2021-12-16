package com.example.tfs.ui.stream.streamcontainer.elm


import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerEvent.Internal
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerEvent.Ui
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class StreamContainerReducer :
    ScreenDslReducer<StreamContainerEvent, Ui, Internal, StreamContainerState, StreamContainerEffect, Command>(
        Ui::class,
        Internal::class
    ) {

    override fun Result.internal(event: Internal) = when (event) {

        is Internal.UpdateSearchQueryComplete -> {
        }

        is Internal.UpdateSearchQueryError -> {
            effects { +StreamContainerEffect.UpdatingSearchQueryError(event.error) }
        }
    }

    override fun Result.ui(event: Ui) = when (event) {

        is Ui.Init -> {
        }

        is Ui.ChangeSearchQuery -> {
            state {
                copy(
                    error = null,
                    searchQuery = event.query
                )
            }
            commands { +Command.UpdateSearchQuery(state.searchQuery) }
        }
    }
}