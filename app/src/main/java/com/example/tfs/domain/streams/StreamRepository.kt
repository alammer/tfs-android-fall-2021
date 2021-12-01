package com.example.tfs.domain.streams

import android.util.Log
import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.Stream
import com.example.tfs.network.models.toLocalStream
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

interface StreamRepository {

    fun loadStreams(query: String, isSubscribed: Boolean): Completable

    /* fun fetchStreams(
         query: String,
         isSubscribed: Boolean,
     ): Observable<List<LocalStream>>*/

    fun getLocalList(): Observable<List<LocalStream>>

    fun selectStream(streamId: Int): Completable
}

class StreamRepositoryImpl : StreamRepository {

    private val networkService = ApiService.create()
    private val database = MessengerDB.instance.localDataDao

    private var subscribed = true
    private var currentQuery = ""

    override fun selectStream(streamId: Int): Completable {
        Log.i("StreamRepository", "Function called: selectStream() $streamId")
        return database.getStream(streamId)
            .flatMapCompletable { localStream ->
                if (localStream.isExpanded) {
                    database.insertStream(localStream.copy(isExpanded = false))
                } else {
                    networkService.getStreamRelatedTopicList(streamId)
                        .map { topicsResponse -> topicsResponse.topicResponseList.map { it.name } }
                        .flatMapCompletable { topicList ->
                            database.insertStream(localStream.copy(isExpanded = true,
                                topics = topicList))
                        }
                }
            }
    }

    override fun loadStreams(
        query: String,
        isSubscribed: Boolean,
    ): Completable {

        subscribed = isSubscribed
        currentQuery = query

        return database.getStreams(isSubscribed)
            .subscribeOn(Schedulers.io())
            .first(emptyList())
            .flatMapCompletable { localStreamList: List<LocalStream> ->
                getRemoteStreams(localStreamList.filter { it.isExpanded }.map { it.streamId })
                    .flatMapCompletable { remoteStreamList ->
                        database.insertStreams(remoteStreamList)
                    }
            }
    }

    override fun getLocalList(): Observable<List<LocalStream>> =
        database.getStreams(subscribed)
            .subscribeOn(Schedulers.io())
            .map { streamList -> streamList.filter { it.streamName.contains(currentQuery) } }

    /*override fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
    ): Observable<List<LocalStream>> {

        return getLocalList()
            .subscribeOn(Schedulers.io())
            .flatMap { localStreamList: List<LocalStream> ->
                getRemoteStreams(localStreamList.filter { it.isExpanded }.map { it.streamId })
                    //DiffUtil?
                    .flatMapSingle { remoteStreamList ->
                        database.insertStreams(remoteStreamList)
                            .andThen(Single.just(remoteStreamList))
                    }
                    .startWith(localStreamList)
            }
    }*/

    private fun getRemoteStreams(
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        if (subscribed) fetchSubscribedStreams(expanded) else fetchRawStreams(expanded)

    private fun fetchSubscribedStreams(
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        networkService.getSubscribedStreams()
            .map { response -> response.streams }
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
    ): Observable<List<LocalStream>> =
        networkService.getRawStreams()
            .map { response -> response.streams }
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