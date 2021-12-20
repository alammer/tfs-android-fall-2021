package com.example.tfs.ui.contacts.elm

import com.example.tfs.database.entity.LocalUser

data class ContactState(
    val contactList: List<LocalUser> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true,
    val isInitial: Boolean = true,
    val query: String = "",
)

sealed class ContactEvent {

    sealed class Ui : ContactEvent() {

        object Init : Ui()

        object RefreshContactList : Ui()

        data class SearchQueryChange(val query: String) : Ui()

        data class ContactClicked(val userId: Int) : Ui()
    }

    sealed class Internal : ContactEvent() {

        data class LocalLoadingComplete(val contactList: List<LocalUser>) : Internal()

        data class RemoteLoadingComplete(val contactList: List<LocalUser>) : Internal()

        data class LoadingError(val error: Throwable) : Internal()
    }
}

sealed class ContactEffect {

    data class LoadingError(val error: Throwable) : ContactEffect()

    data class ShowUser(val userId: Int) : ContactEffect()
}

sealed class Command {

    data class GetLocalContactList(val query: String) : Command()

    data class GetRemoteContactList(val query: String) : Command()
}