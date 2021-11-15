package com.example.tfs.ui.contacts

import com.example.tfs.network.models.User


internal sealed class ContactScreenState {

    class Result(val items: List<User>) : ContactScreenState()

    object Loading : ContactScreenState()

    class Error(val error: Throwable) : ContactScreenState()
}