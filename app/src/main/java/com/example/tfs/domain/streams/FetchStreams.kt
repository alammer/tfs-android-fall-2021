package com.example.tfs.domain.streams

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject




class FetchStreams(private val streamRepository: StreamRepository) {

    private val streamToItemMapper: StreamToItemMapper = StreamToItemMapper()

    fun upload(query: String, isSubcribed: Boolean): Completable {
        return streamRepository.loadStreams(query, isSubcribed)
    }

    fun clickStream(streamId: Int) = streamRepository.selectStream(streamId)

    fun updateStream(): Observable<Int> {
        return streamRepository.updateStream().doAfterNext {it -> Observable.just(it) }
    }

    fun getLocalStreams(isSubcribed: Boolean): Observable<List<StreamListItem>> {
        //TODO("return specific value from DB for empty local cache")
        return streamRepository.getLocalList(isSubcribed)
            .map(streamToItemMapper)
    }
}

class RxBus private constructor() {
    private val publisher = PublishSubject.create<String>()
    fun publish(event: String) {
        publisher.onNext(event)
    }

    // Listen should return an Observable
    fun listen(): Observable<String> {
        return publisher
    }

    companion object {
        private var mInstance: RxBus? = null
        val instance: RxBus?
            get() {
                if (mInstance == null) {
                    mInstance = RxBus()
                }
                return mInstance
            }
    }
}