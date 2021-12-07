package com.example.tfs.ui.stream.elm

import com.example.tfs.domain.streams.StreamInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat
import javax.inject.Inject

class StreamActor @Inject constructor(
    private val streamInteractor: StreamInteractor,
) :
    ActorCompat<Command, StreamEvent.Internal> {

    override fun execute(command: Command): Observable<StreamEvent.Internal> = when (command) {
        is Command.SelectStream -> {
            streamInteractor.clickStream(command.streamId)
                .mapEvents(StreamEvent.Internal.UpdateStreamComplete,
                    StreamEvent.Internal::LoadingError)
        }
        is Command.ObserveStreams -> {
            streamInteractor.getLocalStreams(command.query, command.isSubscribed)
                .mapEvents(StreamEvent.Internal::LoadingComplete,
                    StreamEvent.Internal::LoadingError)
        }
        is Command.ObserveQuery -> {
            streamInteractor.observeQuery()
                .mapEvents(StreamEvent.Internal::QueryChange, StreamEvent.Internal::LoadingError)
        }
    }
}