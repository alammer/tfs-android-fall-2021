package com.example.tfs.domain.streams


import com.example.tfs.common.baseadapter.AdapterItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class StreamInteractor @Inject constructor(
    private val streamRepository: StreamRepository,
    private val rxSearchQueryBus: PublishSubject<String>
) {

    private val streamToUiItemMapper: StreamToUiItemMapper = StreamToUiItemMapper()

    fun updateSearchQuery(query: String): Completable {
        rxSearchQueryBus.onNext(query)
        return Completable.complete()
    }

    fun observeSearchQuery(): Observable<String> {
        return rxSearchQueryBus
    }

    fun clickStream(streamId: Int) = streamRepository.selectStream(streamId)

    fun getLocalStreamList(query: String, isSubscribed: Boolean): Single<List<AdapterItem>> {
        val source =
            if (isSubscribed) {
                streamRepository.getLocalSubscribedStreams(query)
            } else {
                streamRepository.getLocalUnsubscribedStreams(query)
            }
        return source
            .map(streamToUiItemMapper)
    }

    fun updateStreamListFromRemote(query: String, isSubscribed: Boolean): Single<List<AdapterItem>> {
        val source =
            if (isSubscribed) {
                streamRepository.updateSubscribedStreams(query)
            } else {
                streamRepository.updateUnsubscribedStreams(query)
            }
        return source
            .map(streamToUiItemMapper)
    }
}
