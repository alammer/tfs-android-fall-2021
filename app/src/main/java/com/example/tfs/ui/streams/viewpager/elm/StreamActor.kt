package com.example.tfs.ui.streams.viewpager.elm

import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.ui.streams.viewpager.elm.ViewPagerEvent.Internal
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class StreamActor(private val fetchStreams: FetchStreams) : ActorCompat<Command, Internal> {

    override fun execute(command: Command): Observable<Internal> = when (command) {
        is Command.FetchStreams -> {
            fetchStreams.upload(command.query, command.isSubscribed)
                .mapEvents(Internal.StreamsFetchComplete, Internal::StreamsFetchError)
        }
    }
}
