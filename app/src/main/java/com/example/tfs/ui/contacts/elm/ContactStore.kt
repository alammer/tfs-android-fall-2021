package com.example.tfs.ui.contacts.elm

import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

object ContactStore {

    fun provide(actor: ContactActor) = ElmStoreCompat(
        initialState = ContactState(),
        reducer = ContactReducer(),
        actor = actor
    )
}