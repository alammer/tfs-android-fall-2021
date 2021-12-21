package com.example.tfs.domain.stream

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

    fun getLocalSubscribedStreamList(query: String): Single<List<LocalStream>>

    fun getLocalUnsubscribedStreamList(query: String): Single<List<LocalStream>>

    fun searchInLocalStreamList(query: String, isSubscribed: Boolean): Single<List<LocalStream>>

    fun updateSubscribedStreamList(query: String): Single<List<LocalStream>>

    fun updateUnsubscribedStreamList(query: String): Single<List<LocalStream>>

    fun selectStream(streamId: Int): Completable
}

class StreamRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: StreamDataDao,
) : StreamRepository {

    override fun getLocalUnsubscribedStreamList(
        query: String,
    ): Single<List<LocalStream>> = localDao.getUnsubscribedStreams()
        .subscribeOn(Schedulers.io())
        .map { streams -> streams.filter { it.streamName.contains(query) } }


    override fun getLocalSubscribedStreamList(
        query: String,
    ): Single<List<LocalStream>> = localDao.getSubscribedStreams()
        .subscribeOn(Schedulers.io())
        .map { streams -> streams.filter { it.streamName.contains(query) } }

    override fun searchInLocalStreamList(
        query: String,
        isSubscribed: Boolean
    ): Single<List<LocalStream>> {
        val localSource =
            if (isSubscribed) localDao.getSubscribedStreams() else localDao.getUnsubscribedStreams()

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
                    getRemoteRelatedTopics(streamId)
                        .retryWhenError(3, 1)
                        .flatMapCompletable { topicList ->
                            localDao.insertStream(
                                localStream.copy(
                                    isExpanded = topicList.isNotEmpty(),
                                    topics = topicList.map { it.name }
                                )
                            )
                        }
                }
            }
    }

    override fun updateSubscribedStreamList(query: String): Single<List<LocalStream>> {
        return localDao.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMap { localStreamList: List<LocalStream> ->
                getRemoteSubscribedStreams(localStreamList.filter { it.isExpanded }
                    .map { it.streamId })
                    .flatMap { remoteStreamList ->
                        localDao.clearSubscribedStreams()
                            .andThen(localDao.insertStreams(remoteStreamList))
                            .andThen(localDao.getSubscribedStreams()
                                .map { streams -> streams.filter { it.streamName.contains(query) } }
                            )
                    }
            }
    }

    override fun updateUnsubscribedStreamList(query: String): Single<List<LocalStream>> {
        //TODO("MAKE BIFUNCTION")
        return localDao.getAllStreams()
            .subscribeOn(Schedulers.io())
            .flatMap { localStreamList: List<LocalStream> ->
                getRemoteUnsubscribedStreams(localStreamList.filter { it.isExpanded }
                    .map { it.streamId },
                    localStreamList.filter { it.isSubscribed }.map { it.streamId })
                    .flatMap { remoteStreamList ->
                        localDao.clearUnsubscribedStreams()
                            .andThen(localDao.insertStreams(remoteStreamList))
                            .andThen(
                                localDao.getUnsubscribedStreams()
                                    .map { streams -> streams.filter { it.streamName.contains(query) } }
                            )
                    }
            }
    }

    private fun getRemoteSubscribedStreams(
        expanded: List<Int>,
    ): Single<List<LocalStream>> =
        remoteApi.getSubscribedStreams()
            .map { response -> response.subscribedStreams }
            .toObservable()
            .retryWhenError(3, 1)
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) {
                    mapRemoteStreamWithTopicsToLocal(stream, true)
                } else {
                    mapRemoteStreamToLocal(stream, true)
                }
            }
            .toList()


    private fun getRemoteUnsubscribedStreams(
        expanded: List<Int>,
        subsribed: List<Int>
    ): Single<List<LocalStream>> =
        remoteApi.getAllStreams()
            .map { response -> response.allStreams }
            .toObservable()
            .retryWhenError(3, 1)
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .filter { stream -> stream.id !in subsribed }
            .flatMap { stream ->
                if (stream.id in expanded) {
                    mapRemoteStreamWithTopicsToLocal(stream)
                } else {
                    mapRemoteStreamToLocal(stream)
                }
            }
            .toList()

    private fun mapRemoteStreamWithTopicsToLocal(
        stream: RemoteStream,
        isSubscribed: Boolean = false,
    ): Observable<LocalStream> =
        Observable.just(stream).zipWith(
            getRemoteRelatedTopics(stream.id)
                .retry(1)
                .onErrorReturn { emptyList() },
            { expandedStream, topicList -> Pair(expandedStream, topicList) })
            .map { (stream, topicList) ->
                stream.toLocalStream(
                    isSubscribed = isSubscribed,
                    isExpanded = topicList.isNotEmpty(),
                    remoteTopics = topicList,
                )
            }

    private fun getRemoteRelatedTopics(streamId: Int) =
        remoteApi.getStreamRelatedTopicList(streamId)
            .map { response -> response.remoteTopicResponseList }

    private fun mapRemoteStreamToLocal(
        remoteStream: RemoteStream,
        isSubscribed: Boolean = false
    ): Observable<LocalStream> =
        Observable.just(remoteStream.toLocalStream(isSubscribed))

}