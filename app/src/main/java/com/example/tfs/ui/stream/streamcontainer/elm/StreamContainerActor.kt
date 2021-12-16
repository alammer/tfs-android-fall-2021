package com.example.tfs.ui.stream.streamcontainer.elm

import com.example.tfs.domain.stream.StreamInteractor
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerEvent.Internal
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class StreamContainerActor /*@Inject constructor*/(
    private val streamInteractor: StreamInteractor,
) : ActorCompat<Command, Internal> {

    //private val switcher = Switcher() //rxjava3 only?

    override fun execute(command: Command): Observable<Internal> = when (command) {

        is Command.UpdateSearchQuery -> {
            streamInteractor.updateSearchQuery(command.query)
                .mapEvents(Internal.UpdateSearchQueryComplete, Internal::UpdateSearchQueryError)
        }
    }
}
