package com.example.tfs.ui.streams.viewpager.elm

data class ViewPagerState(
    val error: Throwable? = null,
    val isFetching: Boolean = false,
    val isSubscribed: Boolean = true,
    val query: String = "",
    val tabPosition: Int = 0,
)

sealed class ViewPagerEvent {

    sealed class Ui : ViewPagerEvent() {

        object Init : Ui()

        object FetchSubscribedStreams : Ui()

        object FetchRawStreams : Ui()

        data class ChangeSearchQuery(val query: String) : Ui()
    }

    sealed class Internal : ViewPagerEvent() {

        object StreamsFetchComplete : Internal()

        data class StreamsFetchError(val error: Throwable) : Internal()
    }
}

sealed class ViewPagerEffect {

    data class FetchError(val error: Throwable) : ViewPagerEffect()
}

sealed class Command {

    data class FetchStreams(val isSubscribed: Boolean, val query: String) : Command()
}