package com.example.tfs.domain.topic

import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.toLocalPostWithReaction
import com.example.tfs.ui.topic.PagingQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface TopicRepository {

    fun fetchTopic(streamName: String, topicName: String): Observable<List<PostWithReaction>>

    fun uploadTopic(query: PagingQuery): Observable<List<PostWithReaction>>

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
class TopicRepositoryImpl : TopicRepository {

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

    override fun fetchTopic(
        streamName: String,
        topicName: String,
    ): Observable<List<PostWithReaction>> {
        val remoteSource: Observable<List<PostWithReaction>> =
            getRemoteTopic(PagingQuery(streamName, topicName, isInitial = true))

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

    private fun getLocalTopic(): Single<List<PostWithReaction>> =
        database.getPostWithReaction()


    private fun getRemoteTopic(query: PagingQuery) =
        networkService.getRemotePostList(createPostListQuery(query))
            .map { response -> response.remotePostList.map { it.toLocalPostWithReaction() } }
            .toObservable()

    private fun insertTopicToDB(
        remotePostList: List<PostWithReaction>,
    ): Completable =
        database.deleteTopic()
            .andThen(insertPostList(remotePostList))

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