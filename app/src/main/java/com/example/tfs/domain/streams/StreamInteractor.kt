package com.example.tfs.domain.streams

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class StreamInteractor @Inject constructor(private val streamRepository: StreamRepository) {

    private val streamToItemMapper: StreamToItemMapper = StreamToItemMapper()
    private val queryCache = PublishSubject.create<String>()

    fun upload(query: String, isSubcribed: Boolean): Completable {
        queryCache.onNext(query)
        return streamRepository.loadStreams(query, isSubcribed)
    }

    fun observeQuery(): Observable<String> {
        return queryCache
    }

    fun clickStream(streamId: Int) = streamRepository.selectStream(streamId)

    fun getLocalStreams(query: String, isSubscribed: Boolean): Observable<List<StreamListItem>> {
        //TODO("return specific value from DB if local cache is empty ")
        return streamRepository.getLocalList(query, isSubscribed)
            .map(streamToItemMapper)
    }
}
