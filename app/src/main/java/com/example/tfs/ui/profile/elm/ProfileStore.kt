package com.example.tfs.ui.profile.elm


import vivid.money.elmslie.core.ElmStoreCompat

object ProfileStore  {

    fun provide(state: ProfileState, actor: ProfileActor) = ElmStoreCompat(
        initialState = state,
        reducer = ProfileReducer(),
        actor = actor
    )
}