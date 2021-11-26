package com.example.tfs.ui.streams.viewpager.elm


import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer
import com.example.tfs.ui.streams.viewpager.elm.ViewPagerEvent.Ui
import com.example.tfs.ui.streams.viewpager.elm.ViewPagerEvent.Internal

class Reducer :
    ScreenDslReducer<ViewPagerEvent, Ui, Internal, ViewPagerState, ViewPagerEffect, Command>(Ui::class, Internal::class) {

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.StreamsFetchComplete -> {
            state { copy(isFetching = false) }
        }
        is Internal.StreamsFetchError -> {
            state { copy(isFetching = false) }
            effects { +ViewPagerEffect.FetchError(event.error) }
        }
    }

    override fun Result.ui(event: Ui) = when (event) {

        is Ui.Init -> {
            state { copy(isFetching = true, error = null) }
            commands {
                +Command.FetchStreams(isSubscribed = initialState.isSubscribed,
                    query = initialState.query)
            }
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
            if (!state.isSubscribed) {
                Any()
            } else {
                state { copy(isSubscribed = false, isFetching = true, error = null)  }
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