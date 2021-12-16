package com.example.tfs.domain.stream


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
                streamRepository.getLocalSubscribedStreamList(query)
            } else {
                streamRepository.getLocalUnsubscribedStreamList(query)
            }
        return source
            .map(streamToUiItemMapper)
    }

    fun updateStreamListFromRemote(query: String, isSubscribed: Boolean): Single<List<AdapterItem>> {
        val source =
            if (isSubscribed) {
                streamRepository.updateSubscribedStreamList(query)
            } else {
                streamRepository.updateUnsubscribedStreamList(query)
            }
        return source
            .map(streamToUiItemMapper)
    }
}
