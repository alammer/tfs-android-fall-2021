package com.example.tfs.data

import com.example.tfs.domain.streams.StreamItemList
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.Post
import com.example.tfs.network.models.Stream
import com.example.tfs.network.models.toDomainStream
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


interface Repository {

    fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<StreamItemList.StreamItem>>

    fun fetchTopic(
        parentStream: String,
        topicName: String,
    ): Observable<List<Post>>
}

@OptIn(ExperimentalSerializationApi::class)
class RepositoryImpl : Repository {

    private val networkService = ApiService.create()

    override fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<StreamItemList.StreamItem>> =
        if (isSubscribed) fetchSubscribedStreams(expanded, query) else fetchRawStreams(expanded,
            query)

    override fun fetchTopic(parentStream: String, topicName: String): Observable<List<Post>> =
        fetchMessageQueue(parentStream, topicName)

    private fun fetchMessageQueue(parentStream: String, topicName: String) =
        networkService.getTopicMessageQueue(createGetTopicQuery(parentStream, topicName))
            .subscribeOn(Schedulers.io())
            .map { response -> response.postList }
            .toObservable()

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
            networkService.getStreamRelatedTopicList(stream.id),
            { stream, topicList -> Pair(stream, topicList) })
            .map { (stream, topicList) ->
                stream.toDomainStream(stream.name,
                    topicList.topicResponseList,
                    true)
            }

    private fun getStream(stream: Stream): Observable<StreamItemList.StreamItem> =
        Observable.just(stream.toDomainStream(stream.name))

    private fun createGetTopicQuery(parentStream: String, topicName: String) = hashMapOf<String, Any>(
        "anchor" to INITIAL_MESSAGE_QUEUE_ANCHOR,
        "num_before" to INITIAL_MESSAGE_NUM_BEFORE,
        "num_after" to INITIAL_MESSAGE_NUM_AFTER,
        "narrow" to Json.encodeToString(listOf(NarrowObject(parentStream,"stream"), NarrowObject(topicName, "topic")))
    )

    @Serializable
    private data class NarrowObject(
        val operand: String,
        val operator: String,
    )

    companion object {

        const val INITIAL_MESSAGE_QUEUE_ANCHOR = "newest"
        const val INITIAL_MESSAGE_NUM_BEFORE = 100
        const val INITIAL_MESSAGE_NUM_AFTER = 0
    }

}
