package com.example.tfs.ui.stream.elm

import com.example.tfs.ui.stream.adapter.base.StreamListItem

data class StreamState(
    val streamListItem: List<StreamListItem> = emptyList(),
    val error: Throwable? = null,
    val query: String = "",
    val isLoading: Boolean = false,
    val isShowing: Boolean = false,
    val isSubscribed: Boolean = true,
    val isClicked: Boolean = false,
    val isEmpty: Boolean = false,
)

sealed class StreamEvent {

    sealed class Ui : StreamEvent() {

        object Init : Ui()

        object RefreshData : Ui()

        data class ShowFragment(val isSubscribed: Boolean) : Ui()

        data class HideFragment(val isSubscribed: Boolean) : Ui()

        data class ClickOnStream(val streamId: Int) : Ui()

        data class ClickOnTopic(val topicName: String, val streamName: String) : Ui()
    }

    sealed class Internal : StreamEvent() {

        object UpdateStreamComplete : Internal()

        data class QueryChange(val query: String) : Internal()

        data class InitialLoadingComplete(val streams: List<StreamListItem>) : Internal()

        data class InitialLoadingError(val error: Throwable) : Internal()

        data class UpdateDataComplete(val streams: List<StreamListItem>) : Internal()

        data class UpdateDataError(val error: Throwable) : Internal()
    }
}

sealed class StreamEffect {

    data class LoadingDataError(val error: Throwable) : StreamEffect()
    data class ShowTopic(val topicName: String, val streamName: String) : StreamEffect()
}

sealed class Command {

    object ObserveQuery : Command()
    data class InitilaFetchStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class SearchStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class UpdateStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class SelectStream(val streamId: Int) : Command()
}