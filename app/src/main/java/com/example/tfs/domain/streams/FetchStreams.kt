package com.example.tfs.domain.streams

import io.reactivex.Completable
import io.reactivex.Observable

class FetchStreams(private val streamRepository: StreamRepository) {

    private val streamToItemMapper: StreamToItemMapper = StreamToItemMapper()

    fun upload(query: String, isSubcribed: Boolean): Completable {
        return streamRepository.loadStreams(query, isSubcribed)
    }

    fun getLocalStreams(isSubcribed: Boolean): Observable<List<StreamItemList>> {
        return streamRepository.getLocalList(isSubcribed)
            .map(streamToItemMapper)
    }
}