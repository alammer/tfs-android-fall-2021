package com.example.tfs.domain.streams


import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class StreamInteractor @Inject constructor(
    private val streamRepository: StreamRepository,
    private val rxSearchBus: PublishSubject<String>
) {

    private val streamToItemMapper: StreamToItemMapper = StreamToItemMapper()

    fun updateSearch(query: String): Completable {
        rxSearchBus.onNext(query)
        return Completable.complete()
    }

    fun observeQuery(): Observable<String> {
        return rxSearchBus
    }

    fun clickStream(streamId: Int) = streamRepository.selectStream(streamId)

    fun fetchStreams(query: String, isSubscribed: Boolean): Observable<List<StreamListItem>> {
        //TODO("return specific value from DB if local cache is empty ")
        return streamRepository.fetchStreams(query, isSubscribed)
            .map(streamToItemMapper)
    }

    fun getLocalStreams(query: String, isSubscribed: Boolean): Observable<List<StreamListItem>> {
        //TODO("return specific value from DB if local cache is empty ")
        return streamRepository.getLocalList(query, isSubscribed)
            .map(streamToItemMapper)
    }
}
