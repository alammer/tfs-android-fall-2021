package com.example.tfs.ui.profile

import com.example.tfs.database.entity.LocalUser

internal sealed class ProfileScreenState {

    class Result(val user: LocalUser?) : ProfileScreenState()

    object Loading : ProfileScreenState()

    class Error(val error: Throwable) : ProfileScreenState()
}