package com.example.tfs.domain.streams


import com.example.tfs.common.baseadapter.AdapterItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
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

    fun getStreamsFromLocal(query: String, isSubscribed: Boolean): Single<List<AdapterItem>> {
        val source =
            if (isSubscribed) {
                streamRepository.getLocalSubscribedStreams(query)
            } else {
                streamRepository.getLocalUnsubscribedStreams(query)
            }
        return source
            .map(streamToItemMapper)
    }

    fun getStreamsFromRemote(query: String, isSubscribed: Boolean): Single<List<AdapterItem>> {
        val source =
            if (isSubscribed) {
                streamRepository.updateSubscribedStreams(query)
            } else {
                streamRepository.updateUnsubscribedStreams(query)
            }
        return source
            .map(streamToItemMapper)
    }
}
