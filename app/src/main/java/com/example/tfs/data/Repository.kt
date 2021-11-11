package com.example.tfs.data

import com.example.tfs.domain.streams.StreamItemList
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.Stream
import com.example.tfs.network.models.toDomainStream
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


interface Repository {

    fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<StreamItemList.StreamItem>>
}

class RepositoryImpl : Repository {

    private val networkService = ApiService.create()

    override fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<StreamItemList.StreamItem>> =
        if (isSubscribed) fetchSubscribedStreams(expanded, query) else fetchRawStreams(expanded,
            query)


    private fun fetchSubscribedStreams(
        expanded: List<Int>,
        query: String,
    ): Observable<List<StreamItemList.StreamItem>> =
        networkService.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .map { response -> response.streams.filter { it.name.contains(query) } }
            .toObservable()
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) getStreamWithTopics(stream) else getStream(stream)
            }
            .toList()
            .toObservable()

    private fun fetchRawStreams(
        expanded: List<Int>,
        query: String,
    ): Observable<List<StreamItemList.StreamItem>> =
        networkService.getRawStreams()
            .subscribeOn(Schedulers.io())
            .map { response -> response.streams.filter { it.name.contains(query) } }
            .toObservable()
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) getStreamWithTopics(stream) else getStream(stream)
            }
            .toList()
            .toObservable()

    private fun getStreamWithTopics(stream: Stream): Observable<StreamItemList.StreamItem> =
        Observable.zip(Observable.just(stream),
            networkService.getTopics(stream.id),
            { stream, topicList -> Pair(stream, topicList) })
            .map { (stream, topicList) ->
                stream.toDomainStream(stream.name,
                    topicList.topicResponseList,
                    true)
            }


    private fun getStream(stream: Stream): Observable<StreamItemList.StreamItem> =
        Observable.just(stream.toDomainStream(stream.name))


}
