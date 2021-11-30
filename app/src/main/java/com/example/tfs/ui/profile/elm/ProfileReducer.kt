package com.example.tfs.ui.profile.elm

import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class ProfileReducer :
    ScreenDslReducer<ProfileEvent, ProfileEvent.Ui, ProfileEvent.Internal, ProfileState, ProfileEffect, Command>(
        ProfileEvent.Ui::class,
        ProfileEvent.Internal::class) {

    override fun Result.internal(event: ProfileEvent.Internal) = when (event) {
        is ProfileEvent.Internal.ProfileFetchingComplete -> {
            state {
                copy(
                    isFetching = false,
                    user = event.user,
                )
            }
        }
        is ProfileEvent.Internal.ProfileFetchingError -> {
            state { copy(isFetching = false) }
            effects { +ProfileEffect.FetchError }
        }
    }

    override fun Result.ui(event: ProfileEvent.Ui) = when (event) {

        is ProfileEvent.Ui.Init -> {
            state { copy(isFetching = true, error = null) }
        }

        is ProfileEvent.Ui.InitialLoad -> {
            state { copy(userId = event.userId) }
            commands { +Command.GetUser(event.userId) }
        }

        is ProfileEvent.Ui.BackToContacts -> {
            Any()
        }
    }
}