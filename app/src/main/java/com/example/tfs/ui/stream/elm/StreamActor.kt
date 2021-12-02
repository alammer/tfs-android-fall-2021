package com.example.tfs.ui.stream.elm

import com.example.tfs.domain.streams.FetchStreams
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class StreamActor(private val fetchStreams: FetchStreams) :
    ActorCompat<Command, StreamEvent.Internal> {

    override fun execute(command: Command): Observable<StreamEvent.Internal> = when (command) {
        is Command.SelectStream -> {
            fetchStreams.clickStream(command.streamId)
                .mapEvents(StreamEvent.Internal.UpdateStreamComplete, StreamEvent.Internal::LoadingError)
        }
        is Command.ObserveStreams -> {
            fetchStreams.getLocalStreams(command.query, command.isSubscribed)
                .mapEvents(StreamEvent.Internal::LoadingComplete, StreamEvent.Internal::LoadingError)
        }
        is Command.ObserveQuery -> {
            fetchStreams.observeQuery()
                .mapEvents(StreamEvent.Internal::QueryChange, StreamEvent.Internal::LoadingError)
        }
    }
}