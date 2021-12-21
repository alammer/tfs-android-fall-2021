package com.example.tfs.ui.contacts.elm

import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class ContactReducer :
    ScreenDslReducer<ContactEvent, ContactEvent.Ui, ContactEvent.Internal, ContactState, ContactEffect, Command>(
        ContactEvent.Ui::class,
        ContactEvent.Internal::class
    ) {

    override fun Result.internal(event: ContactEvent.Internal) = when (event) {
        is ContactEvent.Internal.LocalLoadingComplete -> {
            if (state.isInitial) {
                state { copy(isInitial = false) }
                if (event.contactList.isEmpty()) {
                    state { copy(isEmpty = true) }
                } else {
                    state { copy(contactList = event.contactList, isEmpty = false) }
                }
                commands { +Command.GetRemoteContactList(state.query) }
            } else {
                if (event.contactList.isEmpty()) {
                    state { copy(isEmpty = true, isLoading = false) }
                } else {
                    state {
                        copy(
                            contactList = event.contactList,
                            isLoading = false,
                            isEmpty = false
                        )
                    }
                }
            }
        }

        is ContactEvent.Internal.RemoteLoadingComplete -> {
            if (event.contactList.isEmpty()) {
                state { copy(isLoading = false, isEmpty = true) }
            } else {
                state { copy(isLoading = false, contactList = event.contactList, isEmpty = false) }
            }
        }

        is ContactEvent.Internal.LoadingError -> {
            state { copy(isLoading = false) }
            effects { +ContactEffect.LoadingError(event.error) }
        }
    }

    override fun Result.ui(event: ContactEvent.Ui) = when (event) {

        is ContactEvent.Ui.Init -> {
            state { copy(isLoading = true, error = null) }
            commands { +Command.GetLocalContactList(state.query) }
        }

        is ContactEvent.Ui.RefreshContactList -> {
            if (state.isLoading.not()) {
                state { copy(isLoading = true, error = null) }
                commands { +Command.GetRemoteContactList(state.query) }
            } else {
                Any()
            }
        }

        is ContactEvent.Ui.SearchQueryChange -> {
            state { copy(isLoading = true, query = event.query, error = null) }
            commands { +Command.GetLocalContactList(event.query) }
        }

        is ContactEvent.Ui.ContactClicked -> {
            state { copy(error = null) }
            effects { +ContactEffect.ShowUser(event.userId) }
        }
    }
}