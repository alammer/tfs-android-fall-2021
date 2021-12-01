package com.example.tfs.ui.contacts.elm

import com.example.tfs.database.entity.LocalUser

data class ContactState(
    val contactList: List<LocalUser> = emptyList(),
    val error: Throwable? = null,
    val isFetching: Boolean = false,
    val query: String = "",
)

sealed class ContactEvent {

    sealed class Ui : ContactEvent() {

        object Init : Ui()

        data class SearchQueryChange(val query: String) : Ui()

        data class ContactClicked(val userId: Int) : Ui()
    }

    sealed class Internal : ContactEvent() {

        data class ContactFetchingComplete(val contactList: List<LocalUser>) : Internal()

        data class ContactFetchingError(val error: Throwable) : Internal()
    }
}

sealed class ContactEffect {

    data class FetchError(val error: Throwable) : ContactEffect()
    data class ShowUser(val userId: Int) : ContactEffect()
}

sealed class Command {

    data class FetchContacts(val query: String) : Command()
}