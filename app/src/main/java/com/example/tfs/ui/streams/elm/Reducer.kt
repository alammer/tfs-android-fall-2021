package com.example.tfs.ui.streams.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class Reducer : DslReducer<Event, State, Effect, Command>(){
    override fun Result.reduce(event: Event): Any? {
        return when (event) {
            is Event.Internal.StreamsFetchComplete -> {
                if (state.items.isEmpty()) {
                    state { copy(error = event.error, isLoading = false, isEmptyState = false) }
                } else {
                    state { copy(items = state.items.removeNextPageLoader(), isLoading = false, isEmptyState = false) }
                    effects { Effect.NextPageLoadError(event.error) }
                }
            }
            is Event.Internal.StreamsFetchError -> {
                val itemsList = state.items.removeNextPageLoader() + event.items
                state { copy(items = itemsList, isLoading = false, error = null, pageNumber = state.pageNumber + 1, isEmptyState = itemsList.isEmpty()) }
            }
            is Event.Ui.LoadFirstPage -> {
                state { copy(isLoading = true, error = null, isEmptyState = false) }
                commands { +Command.LoadPage(state.pageNumber) }
            }
            is Event.Ui.LoadNextPage -> {
                if (state.items.contains(NextPageLoader)) {
                    Any()
                } else {
                    state { copy(items = items + listOf(NextPageLoader), error = null, isLoading = false, isEmptyState = false) }
                    commands { +Command.LoadPage(state.pageNumber) }
                }
            }
        }
    }
    }
}