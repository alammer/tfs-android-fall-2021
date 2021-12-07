package com.example.tfs.ui.profile.elm


import vivid.money.elmslie.core.ElmStoreCompat

object ProfileStore  {

    fun provide(actor: ProfileActor) = ElmStoreCompat(
        initialState = ProfileState(),
        reducer = ProfileReducer(),
        actor = actor
    )
}