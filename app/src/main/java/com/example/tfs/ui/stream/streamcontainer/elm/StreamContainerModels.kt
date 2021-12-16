package com.example.tfs.ui.stream.streamcontainer.elm

data class StreamContainerState(
    val error: Throwable? = null,
    val searchQuery: String = "",
    val tabPosition: Int = 0,
)

sealed class StreamContainerEvent {

    sealed class Ui : StreamContainerEvent() {

        object Init : Ui()

        data class ChangeSearchQuery(val query: String) : Ui()
    }

    sealed class Internal : StreamContainerEvent() {

        object UpdateSearchQueryComplete : Internal()

        data class UpdateSearchQueryError(val error: Throwable) : Internal()
    }
}

sealed class StreamContainerEffect {

    data class UpdatingSearchQueryError(val error: Throwable) : StreamContainerEffect()
}

sealed class Command {

    data class UpdateSearchQuery(val query: String) : Command()
}