package com.example.tfs.domain.streams

import com.example.tfs.database.dao.StreamDataDao
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.RemoteStream
import com.example.tfs.network.models.toLocalStream
import com.example.tfs.util.retryWhenError
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


interface StreamRepository {

    fun fetchSubscribedStreams(query: String): Observable<List<LocalStream>>

    fun fetchAllStreams(query: String): Observable<List<LocalStream>>

    fun observeLocalStreams(query: String, isSubscribed: Boolean): Observable<List<LocalStream>>

    fun selectStream(streamId: Int): Completable
}

class StreamRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: StreamDataDao,
) : StreamRepository {

    override fun fetchAllStreams(
        query: String,
    ): Observable<List<LocalStream>> {

        return localDao.getAllStreams()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localStreamList: List<LocalStream> ->
                getAllStreams(localStreamList.filter { it.isExpanded }.map { it.streamId },
                    localStreamList.filter { it.isSubscribed }.map { it.streamId })
                    .flatMapSingle { remoteStreamList ->
                        localDao.insertStreams(remoteStreamList)
                            .andThen(Single.just(remoteStreamList
                                .filter { it.streamName.contains(query) }
                            ))
                    }
                    .startWith(localStreamList.filter { it.streamName.contains(query) && !it.isSubscribed })
            }
    }

    override fun fetchSubscribedStreams(
        query: String,
    ): Observable<List<LocalStream>> {

        return localDao.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localStreamList: List<LocalStream> ->
                getSubscribedStreams(localStreamList.filter { it.isExpanded }.map { it.streamId })
                    .flatMapSingle { remoteStreamList ->
                        localDao.insertStreams(remoteStreamList)
                            .andThen(Single.just(remoteStreamList.filter {
                                it.streamName.contains(query)
                            }))
                    }
                    .startWith(localStreamList.filter { it.streamName.contains(query) })
            }
    }


    override fun observeLocalStreams(
        query: String,
        isSubscribed: Boolean
    ): Observable<List<LocalStream>> {

        val localSource =
            if (isSubscribed) localDao.observeSubscribedStreams() else localDao.observeAllStreams()

        return localSource
            .subscribeOn(Schedulers.io())
            .map { streamList -> streamList.filter { it.streamName.contains(query) } }
    }

    override fun selectStream(streamId: Int): Completable {
        return localDao.getStream(streamId)
            .flatMapCompletable { localStream ->
                if (localStream.isExpanded) {
                    localDao.insertStream(localStream.copy(isExpanded = false))
                } else {
                    getRelatedTopics(streamId)
                        .flatMapCompletable { topicList ->
                            localDao.insertStream(
                                localStream.copy(
                                    isExpanded = true,
                                    topics = topicList.map { it.name }
                                )
                            )
                        }
                }
            }
    }

    private fun getSubscribedStreams(
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        remoteApi.getSubscribedStreams()
            .map { response -> response.subscribedStreams }
            .toObservable()
            .retryWhenError(3, 1)
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) {
                    getStreamWithTopics(stream, true)
                } else {
                    getStream(stream, true)
                }
            }
            .toList()
            .toObservable()


    private fun getAllStreams(
        expanded: List<Int>,
        subsribed: List<Int>
    ): Observable<List<LocalStream>> =
        remoteApi.getAllStreams()
            .map { response -> response.allStreams }
            .toObservable()
            .retryWhenError(3, 1)
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .filter { stream -> stream.id !in subsribed }
            .flatMap { stream ->
                if (stream.id in expanded) getStreamWithTopics(stream) else getStream(stream)
            }
            .toList()
            .toObservable()

    private fun getStreamWithTopics(
        stream: RemoteStream,
        isSubscribed: Boolean = false,
    ): Observable<LocalStream> =
        Observable.just(stream).zipWith(
            getRelatedTopics(stream.id),
            { expandedStream, topicList -> Pair(expandedStream, topicList) })
            .map { (stream, topicList) ->
                stream.toLocalStream(
                    isSubscribed = isSubscribed,
                    isExpanded = true,
                    remoteTopics = topicList,
                )
            }

    private fun getRelatedTopics(streamId: Int) =
        remoteApi.getStreamRelatedTopicList(streamId)
            .retry(1)
            .map { response -> response.remoteTopicResponseList }
            .onErrorReturn { emptyList() }  //error don't throw down now

    private fun getStream(
        remoteStream: RemoteStream,
        isSubscribed: Boolean = false
    ): Observable<LocalStream> =
        Observable.just(remoteStream.toLocalStream(isSubscribed))

}