package com.example.tfs.ui.streams.elm

data class State(
    val error: Throwable? = null,
    val isFetching: Boolean = false,
    val isSubscribed: Boolean = true,
    val query: String = "",
    val tabPosition: Int = 0,
)

sealed class Event {

    sealed class Ui : Event() {

        object Init : Ui()

        object FetchSubscribedStreams : Ui()

        object FetchRawStreams : Ui()

        data class ChangeSearchQuery(val query: String) : Ui()
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
    data class FetchStreams(val isSubscribed: Boolean, val query: String) : Command()
}