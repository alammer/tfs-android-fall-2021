package com.example.tfs.ui.streams.elm

import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.ui.streams.elm.Event.Internal
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class StreamActor(private val fetchStreams: FetchStreams) : ActorCompat<Command, Internal> {

    override fun execute(command: Command): Observable<Internal> = when (command) {
        is Command.FetchStreams -> fetchStreams.fetch(command.query, command.isSubscribed)
            .mapEvents(Internal.StreamsFetchComplete, Internal::StreamsFetchError)
    }
}
