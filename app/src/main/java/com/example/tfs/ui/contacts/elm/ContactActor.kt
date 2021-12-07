package com.example.tfs.ui.contacts.elm

import com.example.tfs.domain.contacts.ContactInteractor
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat


class ContactActor /*@Inject constructor*/(
    private val contactInteractor: ContactInteractor,
) :
    ActorCompat<Command, ContactEvent.Internal> {
    override fun execute(command: Command): Observable<ContactEvent.Internal> = when (command) {
        is Command.FetchContacts -> {
            contactInteractor.fetch(command.query)
                .mapEvents(ContactEvent.Internal::ContactFetchingComplete,
                    ContactEvent.Internal::ContactFetchingError)
        }
    }
}