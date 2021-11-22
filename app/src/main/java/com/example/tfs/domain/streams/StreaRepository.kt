package com.example.tfs.domain.streams

import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.Stream
import com.example.tfs.network.models.toLocalStream
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

interface StreaRepository {

    fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<LocalStream>>
}

class StreamRepositoryImpl : StreaRepository {

    private val networkService = ApiService.create()
    private val database = MessengerDB.instance.localDataDao

    override fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<LocalStream>> {
        val remoteSource: Observable<List<LocalStream>> =
            getRemoteStreams(query, isSubscribed, expanded)

        return database.getStreams(isSubscribed)
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localStreamList: List<LocalStream> ->
                remoteSource
                    //DiffUtil?
                    .flatMapSingle { remoteStreamList ->
                        database.insertStreams(remoteStreamList)
                            .andThen(Single.just(remoteStreamList))
                    }
                    .startWith(localStreamList)
            }
    }

    private fun getRemoteStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        if (isSubscribed) fetchSubscribedStreams(expanded, query) else fetchRawStreams(expanded,
            query)

    private fun fetchSubscribedStreams(
        expanded: List<Int>,
        query: String,
    ): Observable<List<LocalStream>> =
        networkService.getSubscribedStreams()
            .map { response -> response.streams.filter { it.name.contains(query) } }
            .toObservable()
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) getStreamWithTopics(stream, true) else getStream(stream,
                    true)
            }
            .toList()
            .toObservable()

    private fun fetchRawStreams(
        expanded: List<Int>,
        query: String,
    ): Observable<List<LocalStream>> =
        networkService.getRawStreams()
            .map { response -> response.streams.filter { it.name.contains(query) } }
            .toObservable()
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) getStreamWithTopics(stream) else getStream(stream)
            }
            .toList()
            .toObservable()

    private fun getStreamWithTopics(
        stream: Stream,
        isSubscribed: Boolean = false,
    ): Observable<LocalStream> =
        Observable.zip(Observable.just(stream),
            networkService.getStreamRelatedTopicList(stream.id),
            { expandedStream, topicList -> Pair(expandedStream, topicList) })
            .map { (stream, topicList) ->
                stream.toLocalStream(
                    isSubscribed,
                    topicList.topicResponseList,
                )
            }

    private fun getStream(stream: Stream, isSubscribed: Boolean = false): Observable<LocalStream> =
        Observable.just(stream.toLocalStream(isSubscribed))

}