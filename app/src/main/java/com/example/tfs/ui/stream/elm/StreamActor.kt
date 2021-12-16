package com.example.tfs.ui.stream.elm

import com.example.tfs.domain.stream.StreamInteractor
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

        is Command.SearchInLocalStreamList -> {
            streamInteractor.getLocalStreamList(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::SearchInLocalStreamListComplete,
                    StreamEvent.Internal::UpdateDataError
                )
        }
        is Command.GetLocalStreamList -> {
            streamInteractor.getLocalStreamList(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::LocalLoadingComplete,
                    StreamEvent.Internal::LoadingError
                )
        }
        is Command.UpdateStreamList -> {
            streamInteractor.updateStreamListFromRemote(command.query, command.isSubscribed)
                .mapEvents(
                    StreamEvent.Internal::RemoteLoadingComplete,
                    StreamEvent.Internal::LoadingError
                )
        }
        is Command.ObserveSearchQuery -> {
            streamInteractor.observeSearchQuery()
                .mapEvents(StreamEvent.Internal::SearchQueryChange, StreamEvent.Internal::UpdateDataError)
        }
    }
}