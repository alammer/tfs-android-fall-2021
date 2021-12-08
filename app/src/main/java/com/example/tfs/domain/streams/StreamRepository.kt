package com.example.tfs.domain.streams

import com.example.tfs.database.dao.StreamDataDao
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.Stream
import com.example.tfs.network.models.toLocalStream
import com.example.tfs.util.retryWhenError
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


interface StreamRepository {

    fun fetchStreams(query: String, isSubscribed: Boolean): Completable

    fun getLocalList(query: String, isSubscribed: Boolean): Observable<List<LocalStream>>

    fun selectStream(streamId: Int): Completable
}

class StreamRepositoryImpl /*@Inject constructor*/(
        private val remoteApi: ApiService,
        private val localDao: StreamDataDao,
) : StreamRepository {

    override fun fetchStreams(
            query: String,
            isSubscribed: Boolean,
    ): Completable {

        val localSource =
                if (isSubscribed) localDao.getSubscribedStreams() else localDao.getAllStreams()

        return localSource
                .subscribeOn(Schedulers.io())
                .first(emptyList())
                .flatMapCompletable { localStreamList: List<LocalStream> ->
                    getRemoteStreams(isSubscribed,
                            localStreamList.filter { it.isExpanded }.map { it.streamId })
                            .flatMapCompletable { remoteStreamList ->
                                localDao.insertStreams(remoteStreamList)
                            }
                }
    }

    override fun getLocalList(query: String, isSubscribed: Boolean): Observable<List<LocalStream>> {

        val localSource =
                if (isSubscribed) localDao.getSubscribedStreams() else localDao.getAllStreams()

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
                            localDao.insertStream(localStream.copy(isExpanded = true,
                                topics = topicList.map { it.name }))
                        }
                }
            }
    }

    private fun getRemoteStreams(
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        if (isSubscribed) getSubscribedStreams(expanded) else getRawStreams(expanded)

    private fun getSubscribedStreams(
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        remoteApi.getSubscribedStreams()
            .map { response -> response.streams }
            .toObservable()
            .retryWhenError(3, 1)
            .concatMap { streamList -> Observable.fromIterable(streamList) }
            .flatMap { stream ->
                if (stream.id in expanded) getStreamWithTopics(stream, true) else getStream(stream,
                    true)
            }
            .toList()
            .toObservable()

    private fun getRawStreams(
        expanded: List<Int>,
    ): Observable<List<LocalStream>> =
        remoteApi.getRawStreams()
            .map { response -> response.streams }
            .toObservable()
            .retryWhenError(3, 1)
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
        Observable.just(stream).zipWith(
            getRelatedTopics(stream.id),
            { expandedStream, topicList -> Pair(expandedStream, topicList) })
            .map { (stream, topicList) ->
                stream.toLocalStream(
                    isSubscribed = isSubscribed,
                    isExpanded = true,
                    topics = topicList,
                )
            }

    private fun getRelatedTopics(streamId: Int) =
        remoteApi.getStreamRelatedTopicList(streamId)
            .retry(1)
            .map { response -> response.topicResponseList }
            .onErrorReturn { emptyList()}  //error don't throw now

    private fun getStream(stream: Stream, isSubscribed: Boolean = false): Observable<LocalStream> =
        Observable.just(stream.toLocalStream(isSubscribed))

}