package com.example.tfs.ui.contacts.elm

import vivid.money.elmslie.core.ElmStoreCompat

class ContactStoreFactory(private val contactActor: ContactActor) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = ContactState(),
            reducer = ContactReducer(),
            actor = contactActor
        )
    }

    fun provide() = store
}