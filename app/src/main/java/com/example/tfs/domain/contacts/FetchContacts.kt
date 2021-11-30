package com.example.tfs.domain.contacts

import com.example.tfs.database.entity.LocalUser
import io.reactivex.Maybe
import io.reactivex.Observable

class FetchContacts(private val contactRepository: ContactRepository) {

    fun fetch(query: String): Observable<List<LocalUser>> {
        return contactRepository.fetchUserList(query)
        //TODO("return specific value from DB for empty local cache")
    }

    fun get(userId: Int): Maybe<LocalUser> {
        return contactRepository.getUser(userId)
        //TODO("return specific value from DB for empty local cache")
    }
}