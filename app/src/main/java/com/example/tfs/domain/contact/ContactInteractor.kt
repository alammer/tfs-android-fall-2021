package com.example.tfs.domain.contact

import com.example.tfs.database.entity.LocalUser
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class ContactInteractor @Inject constructor(private val contactRepository: ContactRepository) {

    fun fetch(query: String): Observable<List<LocalUser>> {
        return contactRepository.fetchUserList(query)
        //TODO("return specific value from DB for empty local cache for turn MVI effects")
    }

    fun get(userId: Int): Maybe<LocalUser> {
        return contactRepository.getUser(userId)
        //TODO("return specific value from DB for empty local cache")
    }

    fun getOwner(): Single<LocalUser> {
        return contactRepository.getOwner()
    }
}