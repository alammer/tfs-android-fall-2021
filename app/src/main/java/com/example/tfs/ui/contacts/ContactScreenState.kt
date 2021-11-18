package com.example.tfs.ui.contacts

import com.example.tfs.database.entity.LocalUser

internal sealed class ContactScreenState {

    class Result(val items: List<LocalUser>) : ContactScreenState()

    object Loading : ContactScreenState()

    class Error(val error: Throwable) : ContactScreenState()
}