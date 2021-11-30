package com.example.tfs.domain.contacts

import com.example.tfs.database.entity.LocalUser
import io.reactivex.Observable

class FetchContacts(private val contactRepository: ContactRepository) {

    fun fetch(query: String): Observable<List<LocalUser>> {
        return contactRepository.fetchUserList(query)
    }
}