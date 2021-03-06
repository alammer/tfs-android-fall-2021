package com.example.tfs.ui.profile.elm

import com.example.tfs.domain.contact.ContactInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ProfileActor(
    private val contactInteractor: ContactInteractor,
) :
    ActorCompat<Command, ProfileEvent.Internal> {
    override fun execute(command: Command): Observable<ProfileEvent.Internal> = when (command) {
        is Command.GetUser -> {
            if (command.userId == -1) {
                contactInteractor.getOwner()
                    .mapEvents(
                        ProfileEvent.Internal::ProfileFetchingComplete,
                        ProfileEvent.Internal.ProfileFetchingError
                    )
            } else {
                contactInteractor.get(command.userId)
                    .mapEvents(
                        ProfileEvent.Internal::ProfileFetchingComplete,
                        ProfileEvent.Internal.ProfileFetchingError
                    )
            }
        }
    }
}