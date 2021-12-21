package com.example.tfs.ui.contacts.elm

import com.example.tfs.domain.contact.ContactInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class ContactActor(
    private val contactInteractor: ContactInteractor,
) :
    ActorCompat<Command, ContactEvent.Internal> {
    override fun execute(command: Command): Observable<ContactEvent.Internal> = when (command) {
        is Command.GetLocalContactList -> {
            contactInteractor.getLocalUserList(command.query)
                .mapEvents(
                    ContactEvent.Internal::LocalLoadingComplete,
                    ContactEvent.Internal::LoadingError
                )
        }
        is Command.GetRemoteContactList -> {
            contactInteractor.getRemoteUserList(command.query)
                .mapEvents(
                    ContactEvent.Internal::RemoteLoadingComplete,
                    ContactEvent.Internal::LoadingError
                )
        }
    }
}