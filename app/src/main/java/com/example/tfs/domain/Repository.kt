package com.example.tfs.domain

import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.database.entity.LocalUser
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.domain.contacts.DomainUser
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
    ): Observable<List<LocalStream>>

    fun fetchTopic(streamName: String, topicName: String): Observable<List<PostWithReaction>>

    fun getRemoteUser(userId: Int): Single<DomainUser>

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
    private val database = MessengerDB.instance.localDataDao

    override fun sendMessage(streamName: String, topicName: String, content: String): Completable =
        networkService.sendMessage(streamName, topicName, content)
            .subscribeOn(Schedulers.io())

    override fun addReaction(messageId: Int, emojiName: String, emojiCode: String): Completable =
        networkService.addReaction(messageId, emojiName, emojiCode)
            .subscribeOn(Schedulers.io())

    override fun removeReaction(messageId: Int, emojiName: String, emojiCode: String): Completable =
        networkService.removeReaction(messageId, emojiName, emojiCode)
            .subscribeOn(Schedulers.io())

    override fun fetchStreams(
        query: String,
        isSubscribed: Boolean,
        expanded: List<Int>,
    ): Observable<List<LocalStream>> {
        val remoteSource: Observable<List<LocalStream>> =
            getRemoteStreams(query, isSubscribed, expanded)

        return database.getStreams(isSubscribed)
            .flatMapObservable { localStreamList: List<LocalStream> ->
                remoteSource
/*                    .observeOn(Schedulers.computation())    //DiffUtil maybe?
                   .filter { remoteStreamList: List<LocalStream> ->
                        remoteStreamList != localStreamList
                    }*/
                    .flatMapSingle { remoteStreamList ->
                        database.insertStreams(remoteStreamList)
                            .andThen(Single.just(remoteStreamList))
                    }
                    .startWith(localStreamList)
            }
    }

    override fun fetchTopic(
        streamName: String,
        topicName: String,
    ): Observable<List<PostWithReaction>> {
        val remoteSource: Observable<List<PostWithReaction>> =
            getRemoteTopic(streamName, topicName)

        return getLocalTopic(streamName, topicName)
            .flatMapObservable { localPostList: List<PostWithReaction> ->
                remoteSource
                    .flatMapSingle { remotePostList ->
                        insertPostListToDB(remotePostList)
                            .andThen(Single.just(remotePostList))

                    }
                    .startWith(localPostList)
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
            .subscribeOn(Schedulers.io())
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
            .subscribeOn(Schedulers.io())
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

    private fun getLocalTopic(
        streamName: String,
        topicName: String,
    ): Single<List<PostWithReaction>> =
        database.getPostWithReaction(streamName, topicName)
            .subscribeOn(Schedulers.io())

    private fun getRemoteTopic(
        parentStream: String,
        topicName: String,
    ): Observable<List<PostWithReaction>> =
        getRemotePostList(parentStream, topicName)
            .subscribeOn(Schedulers.io())

    private fun getRemotePostList(streamName: String, topicName: String) =
        networkService.getRemotePostList(createGetTopicQuery(streamName, topicName))
            .subscribeOn(Schedulers.io())
            .map { response -> response.remotePostList.map { it.toLocalPostWithReaction() } }
            .toObservable()

    private fun insertPostListToDB(
        remotePostList: List<PostWithReaction>,
    ): Completable {
        return Completable.concat(remotePostList.map { post ->
            database.insertPost(post.post)
                .andThen(database.insertReactions(post.reaction))
        })
    }

    private fun getLocalUsers(query: String): Observable<List<LocalUser>> =
        database.getAllUsers()
            .observeOn(Schedulers.io())
            .toObservable()

    private fun getRemoteUserList(query: String): Observable<List<LocalUser>> =
        fetchUserList(query)

    override fun getRemoteUser(userId: Int): Single<DomainUser> =
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
            .map { it.toLocalUser() }
            .toList()
            .toObservable()

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
