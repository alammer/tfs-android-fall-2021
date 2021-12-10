package com.example.tfs.ui.stream.elm

import com.example.tfs.domain.streams.StreamListItem

data class StreamState(
    val streamListItem: List<StreamListItem> = emptyList(),
    val error: Throwable? = null,
    val query: String = "",
    val isSubscribed: Boolean = true,
    val isClicked: Boolean = false,
)

sealed class StreamEvent {

    sealed class Ui : StreamEvent() {

        object Init : Ui()

        data class ClickOnStream(val streamId: Int) : Ui()

        data class ClickOnTopic(val topicName: String, val streamName: String) : Ui()
    }

    sealed class Internal : StreamEvent() {

        object UpdateStreamComplete : Internal()

        data class QueryChange(val query: String) : Internal()

        data class LoadingComplete(val streams: List<StreamListItem>) : Internal()

        data class LoadingError(val error: Throwable) : Internal()
    }
}

sealed class StreamEffect {

    data class LoadingDataError(val error: Throwable) : StreamEffect()
    data class ShowTopic(val topicName: String, val streamName: String) : StreamEffect()
}

sealed class Command {

    object ObserveQuery : Command()
    data class ObserveStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class SelectStream(val streamId: Int) : Command()
}