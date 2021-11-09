package com.example.tfs.ui.contacts

import com.example.tfs.domain.contacts.Contact


internal sealed class ContactScreenState {

    class Result(val items: List<Contact>) : ContactScreenState()

    object Loading : ContactScreenState()

    class Error(val error: Throwable) : ContactScreenState()
}