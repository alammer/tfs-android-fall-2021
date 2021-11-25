package com.example.tfs.domain.streams

import io.reactivex.Completable

class FetchStreams (private val streamRepository: StreamRepository) {

    fun fetch(query: String, isSubcribed: Boolean) : Completable {
        return streamRepository.loadStreams(query, isSubcribed)
    }
}