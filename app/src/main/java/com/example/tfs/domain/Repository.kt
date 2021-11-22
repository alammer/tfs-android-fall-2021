package com.example.tfs.domain

import android.util.Log
import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.database.entity.LocalUser
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.domain.contacts.DomainUser
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.*
import com.example.tfs.ui.topic.PagingQuery
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

    fun uploadTopic(query: PagingQuery): Observable<List<PostWithReaction>>

    fun fetchUserList(query: String): Observable<List<LocalUser>>

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

    override fun fetchTopic(
        streamName: String,
        topicName: String,
    ): Observable<List<PostWithReaction>> {
        val remoteSource: Observable<List<PostWithReaction>> =
            getRemoteTopic(PagingQuery(streamName, topicName, isInitial = true))
        
        Log.i("Repository", "Function called: fetchTopic()")

        return getLocalTopic()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localPostList: List<PostWithReaction> ->
                remoteSource
                    .flatMapSingle { remotePostList ->
                        insertTopicToDB(remotePostList)
                            .andThen(Single.just(remotePostList))
                    }
                    .startWith(localPostList)
            }
    }

    override fun uploadTopic(
        query: PagingQuery,
    ): Observable<List<PostWithReaction>> =
        Observable.zip(getRemoteTopic(query), database.getTopicSize().toObservable(),
            { newPage, currentSize -> Pair(newPage, currentSize) })
            .subscribeOn(Schedulers.io())
            .flatMapSingle { (page, size) ->
                addNextPage(query, page, size)
                    .andThen(database.getPostWithReaction())
            }
           /*.subscribeOn(Schedulers.io())    //WTF???
            .map { (page, size) -> addNextPage(query, page, size) }
            .flatMapSingle { database.getPostWithReaction(query.streamName, query.topicName) }*/


    private fun insertPostList(remotePostList: List<PostWithReaction>): Completable {
        return Completable.concat(remotePostList.map { post ->
            database.insertPost(post.post)
                .andThen(database.insertReactions(post.reaction))
        })
    }

    private fun addNextPage(
        query: PagingQuery,
        newPage: List<PostWithReaction>,
        currentSize: Int,
    ): Completable {
        return if (newPage.size + currentSize <= 51) {
            insertPostList(newPage)
        } else {
            if (query.isDownScroll) {
                database.removeFirstPage(newPage.size - (51 - currentSize))
                    .andThen(insertPostList(newPage))
            } else {
                database.removeLastPage(newPage.size - (51 - currentSize))
                    .andThen(insertPostList(newPage))
            }
        }
    }

    override fun fetchUserList(
        query: String,
    ): Observable<List<LocalUser>> {
        val remoteSource: Observable<List<LocalUser>> =
            getRemoteUserList()

        return getLocalUserList()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localUserList: List<LocalUser> ->
                remoteSource
/*                    .observeOn(Schedulers.computation())    //DiffUtil maybe?
                   .filter { remoteStreamList: List<LocalStream> ->
                        remoteStreamList != localStreamList
                    }*/
                    .flatMapSingle { remoteUserList ->
                        database.insertAllUsers(remoteUserList)
                            .andThen(Single.just(remoteUserList.filter { it.userName.contains(query) }))
                    }
                    .startWith(localUserList.filter { it.userName.contains(query) })
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

    private fun getLocalTopic(): Single<List<PostWithReaction>> {
        Log.i("Repository", "Function called: getLocalTopic()")
        return database.getPostWithReaction()
            /*.doAfterSuccess { Log.i("Repository", "Function called: getLocalTopic() Success $it") }
            .doOnError { Log.i("Repository", "Function called: getLocalTopic() Error $it") }*/
    }

    private fun getRemoteTopic(query: PagingQuery) =
        networkService.getRemotePostList(createPostListQuery(query))
            .map { response -> response.remotePostList.map { it.toLocalPostWithReaction() } }
            .toObservable()

    private fun insertTopicToDB(
        remotePostList: List<PostWithReaction>,
    ): Completable =
        database.deleteTopic()
            .andThen(insertPostList(remotePostList))

    private fun getLocalUserList(): Single<List<LocalUser>> =
        database.getAllUsers()
            .subscribeOn(Schedulers.io())

    private fun getRemoteUserList(): Observable<List<LocalUser>> =
        networkService.getAllUsers()
            .map { response -> response.userList }
            .toObservable()
            .concatMap { userList -> Observable.fromIterable(userList) }
            .map { it.toLocalUser() }
            .toList()
            .toObservable()

    override fun getRemoteUser(userId: Int): Single<DomainUser> =
        getUserWithPresence(userId)
            .subscribeOn(Schedulers.io())

    private fun getUserWithPresence(userId: Int) =
        Single.zip(networkService.getUser(userId),
            networkService.getUserPresence(userId),
            { user, presence -> Pair(user, presence) })
            .map { (user, presence) ->
                user.toDomainUser(presence.userPresence.userPresence)
            }

    private fun createPostListQuery(query: PagingQuery) =
        if (query.isInitial) {
            hashMapOf<String, Any>(
                "anchor" to "newest",
                "num_before" to 50,
                "num_after" to 0,
                "narrow" to Json.encodeToString(listOf(NarrowObject(query.streamName, "stream"),
                    NarrowObject(query.topicName, "topic")))
            )
        } else {
            hashMapOf<String, Any>(
                "anchor" to query.anchorId,
                "num_before" to if (query.isDownScroll) 0 else 20,
                "num_after" to if (query.isDownScroll) 20 else 0,
                "narrow" to Json.encodeToString(listOf(NarrowObject(query.streamName, "stream"),
                    NarrowObject(query.topicName, "topic")))
            )
        }

    @Serializable
    private data class NarrowObject(
        val operand: String,
        val operator: String,
    )
}
