package com.example.tfs.ui.stream.streamcontainer.elm

data class StreamContainerState(
    val error: Throwable? = null,
    val isFetching: Boolean = false,
    val isSubscribed: Boolean = true,
    val query: String = "",
    val tabPosition: Int = 0,
)

sealed class StreamContainerEvent {

    sealed class Ui : StreamContainerEvent() {

        object Init : Ui()

        object FetchSubscribedStreams : Ui()

        object FetchRawStreams : Ui()

        data class ChangeSearchQuery(val query: String) : Ui()
    }

    sealed class Internal : StreamContainerEvent() {

        object StreamsFetchComplete : Internal()

        data class StreamUpdate(val streamId: Int) : Internal()

        object StreamUpdateComplete : Internal()

        data class StreamsFetchError(val error: Throwable) : Internal()
    }
}

sealed class StreamContainerEffect {

    data class FetchError(val error: Throwable) : StreamContainerEffect()
}

sealed class Command {

    data class FetchStreams(val isSubscribed: Boolean, val query: String) : Command()

    object UpdateStream : Command()
}