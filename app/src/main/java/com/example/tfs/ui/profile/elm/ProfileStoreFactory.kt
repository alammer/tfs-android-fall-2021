package com.example.tfs.ui.profile.elm


import vivid.money.elmslie.core.ElmStoreCompat

class ProfileStoreFactory(private val profileActor: ProfileActor) {

    /*private val store by lazy {
        ElmStoreCompat(
            initialState = ProfileState(),
            reducer = ProfileReducer(),
            actor = profileActor
        )
    }*/

    fun provide() = ElmStoreCompat(
        initialState = ProfileState(),
        reducer = ProfileReducer(),
        actor = profileActor
    )
}