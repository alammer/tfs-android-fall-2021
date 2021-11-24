package com.example.tfs.domain.streams

import io.reactivex.Completable

class FetchStreams (private val streamRepository: StreamRepository) {

    fun getDomainStreamList(isSubcribed: Boolean, query: String) : Completable {
        return Completable.complete()
    }
}