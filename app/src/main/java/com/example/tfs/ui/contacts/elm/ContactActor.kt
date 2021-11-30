package com.example.tfs.ui.contacts.elm

import com.example.tfs.domain.contacts.FetchContacts
import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat

class ContactActor(private val fetchContacts: FetchContacts) :
    ActorCompat<Command, ContactEvent.Internal> {
    override fun execute(command: Command): Observable<ContactEvent.Internal> = when (command) {
        is Command.FetchContacts -> {
            fetchContacts.fetch(command.query)
                .mapEvents(ContactEvent.Internal::ContactFetchingComplete,
                    ContactEvent.Internal::ContactFetchingError)
        }
        is Command.ShowContact -> TODO()
        Command.GoAway -> TODO()
    }
}