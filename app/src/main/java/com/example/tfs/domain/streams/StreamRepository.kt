package com.example.tfs.domain.streams

import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.Stream
import com.example.tfs.network.models.toLocalStream
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

interface StreamRepository {

    fun loadStreams(query: String, isSubscribed: Boolean): Completable

    fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
    ): Observable<List<LocalStream>>

    fun getLocalList(isSubscribed: Boolean): Observable<List<LocalStream>>
}

class StreamRepositoryImpl : StreamRepository {

    private val networkService = ApiService.create()
    private val database = MessengerDB.instance.localDataDao

    override fun loadStreams(
        query: String,
        isSubscribed: Boolean,
    ): Completable {

        return database.getStreams(isSubscribed)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { localStreamList: List<LocalStream> ->
                getRemoteStreams(query,
                    isSubscribed,
                    localStreamList.filter { it.isExpanded }.map { it.streamId })
                    .flatMapCompletable { remoteStreamList ->
                        database.insertStreams(remoteStreamList)
                    }
            }
    }

    override fun getLocalList(isSubscribed: Boolean): Observable<List<LocalStream>> =
        database.getStreams(isSubscribed)
            .subscribeOn(Schedulers.io())
            .toObservable()

    override fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
    ): Observable<List<LocalStream>> {

        return getLocalList(isSubscribed)
            .subscribeOn(Schedulers.io())
            .flatMap { localStreamList: List<LocalStream> ->
                getRemoteStreams(query,
                    isSubscribed,
                    localStreamList.filter { it.isExpanded }.map { it.streamId })
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
        if (isSubscribed) fetchSubscribedStreams(query, expanded) else fetchRawStreams(query,
            expanded)

    private fun fetchSubscribedStreams(
        query: String,
        expanded: List<Int>,
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
        query: String,
        expanded: List<Int>,
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
                    isSubscribed = isSubscribed,
                    isExpanded = true,
                    topics = topicList.topicResponseList,
                )
            }

    private fun getStream(stream: Stream, isSubscribed: Boolean = false): Observable<LocalStream> =
        Observable.just(stream.toLocalStream(isSubscribed))

}