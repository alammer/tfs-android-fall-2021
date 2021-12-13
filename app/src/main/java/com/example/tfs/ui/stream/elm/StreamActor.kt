package com.example.tfs.ui.stream.elm

import com.example.tfs.domain.streams.StreamInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class StreamActor /*@Inject constructor*/(
    private val streamInteractor: StreamInteractor,
) :
    ActorCompat<Command, StreamEvent.Internal> {

    override fun execute(command: Command): Observable<StreamEvent.Internal> = when (command) {
        is Command.SelectStream -> {
            streamInteractor.clickStream(command.streamId)
                .mapEvents(
                    StreamEvent.Internal.UpdateStreamComplete,
                    StreamEvent.Internal::UpdateDataError
                )
        }

        is Command.SearchStreams -> {
            streamInteractor.observeStreams(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::UpdateDataComplete,
                    StreamEvent.Internal::UpdateDataError
                )
        }
        is Command.InitilaFetchStreams -> {
            streamInteractor.fetchStreams(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::InitialLoadingComplete,
                    StreamEvent.Internal::InitialLoadingError
                )
        }
        is Command.ObserveQuery -> {
            streamInteractor.observeQuery()
                .mapEvents(StreamEvent.Internal::QueryChange, StreamEvent.Internal::UpdateDataError)
        }
    }
}