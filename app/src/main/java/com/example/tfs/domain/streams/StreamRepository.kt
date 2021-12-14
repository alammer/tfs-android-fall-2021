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

    fun getLocalSubscribedStreams(query: String): Single<List<LocalStream>>

    fun getLocalUnsubscribedStreams(query: String): Single<List<LocalStream>>

    fun searchStreams(query: String, isSubscribed: Boolean): Single<List<LocalStream>>

    fun updateSubscribedStreams(query: String): Single<List<LocalStream>>

    fun updateUnsubscribedStreams(query: String): Single<List<LocalStream>>

    fun selectStream(streamId: Int): Completable
}

class StreamRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: StreamDataDao,
) : StreamRepository {

    override fun getLocalUnsubscribedStreams(
        query: String,
    ): Single<List<LocalStream>> {

        return localDao.getUnsubscribedStreams()
            .subscribeOn(Schedulers.io())
            .map { streams -> streams.filter { it.streamName.contains(query) } }
    }

    override fun getLocalSubscribedStreams(
        query: String,
    ): Single<List<LocalStream>> {

        return localDao.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .map { streams -> streams.filter { it.streamName.contains(query) } }

    }


    override fun searchStreams(
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

    override fun updateSubscribedStreams(query: String): Single<List<LocalStream>> {

        return localDao.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMap { localStreamList: List<LocalStream> ->
                getRemoteSubscribedStreams(localStreamList.filter { it.isExpanded }
                    .map { it.streamId })
                    .flatMap { remoteStreamList ->
                        localDao.clearSubscribedStreams()
                            .andThen(localDao.insertStreams(remoteStreamList))
                            .andThen(Single.just(remoteStreamList.filter {
                                it.streamName.contains(query)
                            }))
                    }
            }
    }

    override fun updateUnsubscribedStreams(query: String): Single<List<LocalStream>> {

        //TODO("MAKE BIFUNCTION")
        return localDao.getAllStreams()
            .subscribeOn(Schedulers.io())
            .flatMap { localStreamList: List<LocalStream> ->
                getRemoteUnsubcribedStreams(localStreamList.filter { it.isExpanded }
                    .map { it.streamId },
                    localStreamList.filter { it.isSubscribed }.map { it.streamId })
                    .flatMap { remoteStreamList ->
                        localDao.clearUnsubscribedStreams()
                            .andThen(localDao.insertStreams(remoteStreamList))
                            .andThen(Single.just(remoteStreamList.filter {
                                it.streamName.contains(query)
                            }))
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


    private fun getRemoteUnsubcribedStreams(
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
            getRemoteRelatedTopics(stream.id),
            { expandedStream, topicList -> Pair(expandedStream, topicList) })
            .map { (stream, topicList) ->
                stream.toLocalStream(
                    isSubscribed = isSubscribed,
                    isExpanded = true,
                    remoteTopics = topicList,
                )
            }

    private fun getRemoteRelatedTopics(streamId: Int) =
        remoteApi.getStreamRelatedTopicList(streamId)
            .retry(1)
            .map { response -> response.remoteTopicResponseList }
            .onErrorReturn { emptyList() }  //error don't throw down now

    private fun mapRemoteStreamToLocal(
        remoteStream: RemoteStream,
        isSubscribed: Boolean = false
    ): Observable<LocalStream> =
        Observable.just(remoteStream.toLocalStream(isSubscribed))

}