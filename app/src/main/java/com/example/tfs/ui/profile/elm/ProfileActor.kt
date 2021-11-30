package com.example.tfs.ui.profile.elm

import com.example.tfs.domain.contacts.FetchContacts
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ProfileActor(private val fetchContacts: FetchContacts) :
    ActorCompat<Command, ProfileEvent.Internal> {
    override fun execute(command: Command): Observable<ProfileEvent.Internal> = when (command) {
        is Command.GetUser -> {
            fetchContacts.get(command.userId)
                .mapEvents(ProfileEvent.Internal::ProfileFetchingComplete,
                    ProfileEvent.Internal.ProfileFetchingError)
        }
        Command.BackToContacts -> TODO()
    }
}