package com.example.tfs.ui.streams.elm

data class State(
    val error: Throwable? = null,
    val isFetching: Boolean = false,
    val isSubscribed: Boolean = true,
    val query: String = "",
    val tabPosition: Int = 0,
) {

    private companion object {
        const val INITIAL_PAGE = 0
    }
}

sealed class Event {

    sealed class Ui : Event() {

        object Init : Ui()

        object ChangeStreamPage : Ui()

        object ChangeSearchQuery : Ui()
    }

    sealed class Internal : Event() {

        object StreamsFetchComplete : Internal()

        data class StreamsFetchError(val error: Throwable) : Internal()
    }
}

sealed class Effect {
    data class FetchError(val error: Throwable) : Effect()
}

sealed class Command {
    data class StreamsFetch(val isSubscribed: Boolean, val query: String) : Command()
}