package com.example.tfs.ui.profile.elm

import com.example.tfs.database.entity.LocalUser

data class ProfileState(
    val user: LocalUser? = null,
    val error: Throwable? = null,
    val isFetching: Boolean = false,
    val userId: Int = -1,
)

sealed class ProfileEvent {

    sealed class Ui : ProfileEvent() {

        object Init : Ui()

        object BackToContacts : Ui()

        data class InitialLoad(val userId: Int) : Ui()
    }

    sealed class Internal : ProfileEvent() {


        data class ProfileFetchingComplete(val user: LocalUser) : Internal()

        object ProfileFetchingError : Internal()
    }
}

sealed class ProfileEffect {

    object FetchError : ProfileEffect()
    object BackNavigation : ProfileEffect()
}

sealed class Command {

    data class GetUser(val userId: Int) : Command()
}