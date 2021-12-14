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
            streamInteractor.getStreamsFromLocal(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::SearchStreamsComplete,
                    StreamEvent.Internal::UpdateDataError
                )
        }
        is Command.GetLocalStreams -> {
            streamInteractor.getStreamsFromLocal(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::LocalLoadingComplete,
                    StreamEvent.Internal::LoadingError
                )
        }
        is Command.GetRemoteStreams -> {
            streamInteractor.getStreamsFromRemote(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::RemoteLoadingComplete,
                    StreamEvent.Internal::LoadingError
                )
        }
        is Command.ObserveQuery -> {
            streamInteractor.observeQuery()
                .mapEvents(StreamEvent.Internal::QueryChange, StreamEvent.Internal::UpdateDataError)
        }
    }
}