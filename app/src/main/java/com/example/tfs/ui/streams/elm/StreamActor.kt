package com.example.tfs.ui.streams.elm

import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.ui.streams.elm.Event.Internal.StreamsFetchComplete
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class StreamActor(private val fetchStreams: FetchStreams) : ActorCompat<Command, Event.Internal> {

    override fun execute(command: Command): Observable<Event.Internal> = when (command) {
        is Command.StreamsFetch -> fetchStreams.getDomainStreamList(command.isSubscribed, command.query)
            .mapEvents(StreamsFetchComplete, Event.Internal::ErrorStreamsFetching)
            //.mapEvents(Event.Internal::StreamsFetchComplete, Event.Internal.ErrorStreamsFetching)
    }
}