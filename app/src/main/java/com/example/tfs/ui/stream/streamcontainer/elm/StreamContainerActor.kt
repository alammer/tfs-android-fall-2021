package com.example.tfs.ui.stream.streamcontainer.elm

import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerEvent.Internal
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class StreamContainerActor(private val fetchStreams: FetchStreams) : ActorCompat<Command, Internal> {

    override fun execute(command: Command): Observable<Internal> = when (command) {
        is Command.FetchStreams -> {
            fetchStreams.upload(command.query, command.isSubscribed)
                .mapEvents(Internal.StreamsFetchComplete, Internal::StreamsFetchError)
        }
        /*is Command.UpdateStream -> {
            fetchStreams.updateStream()
                .mapEvents(Internal::StreamUpdate, Internal::StreamsFetchError)
        }*/
    }
}
