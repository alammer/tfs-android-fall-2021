package com.example.tfs.ui.stream.streamcontainer.elm

data class StreamContainerState(
    val error: Throwable? = null,
    val query: String = "",
    val tabPosition: Int = 0,
)

sealed class StreamContainerEvent {

    sealed class Ui : StreamContainerEvent() {

        object Init : Ui()

        data class ChangeSearchQuery(val query: String) : Ui()
    }

    sealed class Internal : StreamContainerEvent() {

        object UpdateQueryComplete : Internal()

        data class UpdateQueryError(val error: Throwable) : Internal()
    }
}

sealed class StreamContainerEffect {

    data class QueryError(val error: Throwable) : StreamContainerEffect()
}

sealed class Command {

    data class UpdateSearch(val query: String) : Command()
}