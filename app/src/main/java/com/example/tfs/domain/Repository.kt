package com.example.tfs.data

import com.example.tfs.domain.contacts.DomainUser
import com.example.tfs.domain.streams.StreamItemList
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
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

    fun fetchUsers(query: String): Observable<List<User>>

    fun fetchUser(userId: Int): Single<DomainUser>

    fun sendMessage(
        streamName: String,
        topicName: String,
        content: String,
    ): Completable

    fun addReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Completable

    fun removeReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Completable
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

    override fun fetchUsers(query: String): Observable<List<User>> =
        fetchUserList(query)


    override fun fetchTopic(parentStream: String, topicName: String): Observable<List<Post>> =
        fetchMessageQueue(parentStream, topicName)

    override fun sendMessage(streamName: String, topicName: String, content: String): Completable =
        networkService.sendMessage(streamName, topicName, content)
            .subscribeOn(Schedulers.io())

    override fun addReaction(messageId: Int, emojiName: String, emojiCode: String): Completable =
        networkService.addReaction(messageId, emojiName, emojiCode)
            .subscribeOn(Schedulers.io())

    override fun removeReaction(messageId: Int, emojiName: String, emojiCode: String): Completable =
        networkService.removeReaction(messageId, emojiName, emojiCode)
            .subscribeOn(Schedulers.io())

    override fun fetchUser(userId: Int): Single<DomainUser> =
        fetchUserPresence(userId)

    private fun fetchUserPresence(userId: Int) =
        Single.zip(networkService.getUser(userId),
            networkService.getUserPresence(userId),
            { user, presence -> Pair(user, presence) })
            .map { (user, presence) ->
                user.toDomainUser(presence.userPresence.userPresence)
            }
            .subscribeOn(Schedulers.io())


    private fun fetchUserList(query: String) =
        networkService.getAllUsers()
            .subscribeOn(Schedulers.io())
            .map { response -> response.userList }
            .toObservable()
            .concatMap { userList -> Observable.fromIterable(userList) }
            .filter { it.name.contains(query) }
            .toList()
            .toObservable()


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
            { expandedStream, topicList -> Pair(expandedStream, topicList) })
            .map { (stream, topicList) ->
                stream.toDomainStream(stream.name,
                    topicList.topicResponseList,
                    true)
            }

    private fun getStream(stream: Stream): Observable<StreamItemList.StreamItem> =
        Observable.just(stream.toDomainStream(stream.name))

    private fun createGetTopicQuery(parentStream: String, topicName: String) =
        hashMapOf<String, Any>(
            "anchor" to INITIAL_MESSAGE_QUEUE_ANCHOR,
            "num_before" to INITIAL_MESSAGE_NUM_BEFORE,
            "num_after" to INITIAL_MESSAGE_NUM_AFTER,
            "narrow" to Json.encodeToString(listOf(NarrowObject(parentStream, "stream"),
                NarrowObject(topicName, "topic")))
        )

    @Serializable
    private data class NarrowObject(
        val operand: String,
        val operator: String,
    )

    companion object {

        const val INITIAL_MESSAGE_QUEUE_ANCHOR = "newest"
        const val INITIAL_MESSAGE_NUM_BEFORE = 500
        const val INITIAL_MESSAGE_NUM_AFTER = 0
    }

}
