package com.example.tfs.domain.contact

import com.example.tfs.database.entity.LocalUser
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class ContactInteractor @Inject constructor(private val contactRepository: ContactRepository) {

    fun getRemoteUserList(query: String): Single<List<LocalUser>> {
        return contactRepository.getRemoteUserList(query)
    }

    fun getLocalUserList(query: String): Single<List<LocalUser>> {
        return contactRepository.getLocalUserList(query)
    }

    fun get(userId: Int): Maybe<LocalUser> {
        return contactRepository.getUser(userId)
    }

    fun getOwner(): Single<LocalUser> {
        return contactRepository.getOwner()
    }
}