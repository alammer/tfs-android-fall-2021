package com.example.tfs.ui.stream.elm

import com.example.tfs.common.baseadapter.AdapterItem

data class StreamState(
    val streamListItem: List<AdapterItem> = emptyList(),
    val error: Throwable? = null,
    val query: String = "",
    val isInitial: Boolean = true,
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

        data class LocalLoadingComplete(val streams: List<AdapterItem>) : Internal()

        data class RemoteLoadingComplete(val streams: List<AdapterItem>) : Internal()

        data class LoadingError(val error: Throwable) : Internal()

        data class SearchStreamsComplete(val streams: List<AdapterItem>) : Internal()

        data class UpdateDataError(val error: Throwable) : Internal()
    }
}

sealed class StreamEffect {

    data class LoadingDataError(val error: Throwable) : StreamEffect()
    data class ShowTopic(val topicName: String, val streamName: String) : StreamEffect()
}

sealed class Command {

    object ObserveQuery : Command()
    data class GetLocalStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class SearchStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class GetRemoteStreams(val query: String, val isSubscribed: Boolean) : Command()
    data class SelectStream(val streamId: Int) : Command()
}