package com.example.tfs.ui.stream.elm

import com.example.tfs.domain.streams.StreamListItem

data class StreamState(
    val streamListItem: List<StreamListItem> = emptyList(),
    val error: Throwable? = null,
    val isClicked: Boolean = false,
    val isLoading: Boolean = false,
)

sealed class StreamEvent {

    sealed class Ui : StreamEvent() {

        object Init : Ui()

        data class ClickOnStream(val streamId: Int) : Ui()

        data class ClickOnTopic(val streamId: Int, val topicId: Int) : Ui()
    }

    sealed class Internal : StreamEvent() {

        object UpdateStreamComplete : Internal()

        data class LoadingComplete(val streams: List<StreamListItem>) : Internal()

        data class LoadingError(val error: Throwable) : Internal()
    }
}

sealed class StreamEffect {

    data class LoadingDataError(val error: Throwable) : StreamEffect()
}

sealed class Command {

    object ObserveStreams : Command()
    data class SelectStream(val streamId: Int) : Command()
    /*data class SelectTopic(val streamId: Int, val topicId: Int) : Command()*/
}