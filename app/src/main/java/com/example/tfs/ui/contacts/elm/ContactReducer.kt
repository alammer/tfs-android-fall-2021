package com.example.tfs.ui.contacts.elm

import android.util.Log
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class ContactReducer :
    ScreenDslReducer<ContactEvent, ContactEvent.Ui, ContactEvent.Internal, ContactState, ContactEffect, Command>(
        ContactEvent.Ui::class,
        ContactEvent.Internal::class) {

    override fun Result.internal(event: ContactEvent.Internal) = when (event) {
        is ContactEvent.Internal.ContactFetchingComplete -> {
            state {
                Log.i("ContactReducer", "Function called: internal() fetch complete")
                copy(
                    isFetching = false,
                    contactList = event.contactList,
                )
            }
        }
        is ContactEvent.Internal.ContactFetchingError -> {
            Log.i("ContactReducer", "Function called: internal() fetch error")
            state { copy(isFetching = false) }
            effects { +ContactEffect.FetchError(event.error) }
        }
    }

    override fun Result.ui(event: ContactEvent.Ui) = when (event) {

        is ContactEvent.Ui.Init -> {
            state { copy(isFetching = true, error = null) }
            commands { +Command.FetchContacts(state.query) }
        }

        is ContactEvent.Ui.SearchQueryChange -> {
            state { copy(isFetching = true, query = event.query, error = null) }
            commands { +Command.FetchContacts(event.query) }
        }

        is ContactEvent.Ui.ContactClicked -> {
            Any()
        }

        is ContactEvent.Ui.BackToStack -> {
            Any()
        }
    }
}