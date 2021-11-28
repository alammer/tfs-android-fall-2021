package com.example.tfs.domain.topic

import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalPost
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.toLocalPostWithReaction
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

    fun fetchNextPage(streamName: String, topicName: String, anchorId: Int): Observable<List<PostWithReaction>>

    fun fetchPrevPage(streamName: String, topicName: String, anchorId: Int): Observable<List<PostWithReaction>>

    fun sendMessage(
        streamName: String,
        topicName: String,
        content: String,
    ): Observable<List<PostWithReaction>>

    fun addReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
        ownerId: Int,
    ): Observable<List<PostWithReaction>>

    fun removeReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
        ownerId: Int,
    ): Observable<List<PostWithReaction>>
}

@OptIn(ExperimentalSerializationApi::class)
class TopicRepositoryImpl : TopicRepository {

    private val networkService = ApiService.create()
    private val database = MessengerDB.instance.localDataDao

    override fun sendMessage(
        streamName: String,
        topicName: String,
        content: String,
    ): Observable<List<PostWithReaction>> =
        networkService.sendMessage(streamName, topicName, content)
            .subscribeOn(Schedulers.io())
            .andThen(database.insertPost(LocalPost(senderId = 1,
                timeStamp = System.currentTimeMillis() * 1000L,
                content = content)))
            .andThen(getLocalTopic())

    override fun addReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Observable<List<PostWithReaction>> =
        networkService.addReaction(messageId, emojiName, emojiCode)
            .subscribeOn(Schedulers.io())
            .andThen(database.insertReaction(LocalReaction(postId = messageId,
                name = emojiName,
                code = emojiCode,
                userId = 1)))
            .andThen(getLocalTopic())


    override fun removeReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Observable<List<PostWithReaction>> =
        networkService.removeReaction(messageId, emojiName, emojiCode)
            .subscribeOn(Schedulers.io())
            .andThen(database.deleteReaction(messageId, emojiCode, 1))
            .andThen(getLocalTopic())

    override fun fetchTopic(
        streamName: String,
        topicName: String,
    ): Observable<List<PostWithReaction>> {
        val remoteSource: Observable<List<PostWithReaction>> =
            getRemoteTopic(latestTopicQuery(streamName, topicName))

        return getLocalTopic()
            .subscribeOn(Schedulers.io())
            .flatMap { localPostList: List<PostWithReaction> ->
                remoteSource
                    .flatMapSingle { remotePostList ->
                        insertTopicToDB(remotePostList)
                            .andThen(Single.just(remotePostList))
                    }
                    .startWith(localPostList)
            }
    }

    override fun fetchNextPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Observable<List<PostWithReaction>> =
        fetchPage(nextPageQuery(streamName,topicName, anchorId))

    override fun fetchPrevPage(
        streamName: String,
        topicName: String,
        anchorId: Int
    ): Observable<List<PostWithReaction>> =
        fetchPage(prevPageQuery(streamName,topicName, anchorId), isNext = false)

    private fun fetchPage(
       query: HashMap<String, Any>,
       isNext: Boolean = true
    ): Observable<List<PostWithReaction>>  =
        Observable.zip(getRemoteTopic(query), database.getTopicSize().toObservable(),
            { newPage, currentSize -> Pair(newPage, currentSize) })
            .subscribeOn(Schedulers.io())
            .flatMap { (page, size) ->
                addPage(page, size, isNext)
                    .andThen(database.getPostWithReaction())
            }

    /*.subscribeOn(Schedulers.io())    //WTF???
     .map { (page, size) -> addNextPage(query, page, size) }
     .flatMapSingle { database.getPostWithReaction(query.streamName, query.topicName) }*/

    private fun addPage(
        newPage: List<PostWithReaction>,
        currentSize: Int,
        isNext: Boolean = true
    ): Completable {
        return if (newPage.size + currentSize <= 51) {
            insertPostList(newPage)
        } else {
            if (isNext) {
                database.removeFirstPage(newPage.size - (51 - currentSize))
                    .andThen(insertPostList(newPage))
            } else {
                database.removeLastPage(newPage.size - (51 - currentSize))
                    .andThen(insertPostList(newPage))
            }
        }
    }

    private fun insertPostList(remotePostList: List<PostWithReaction>): Completable {
        return Completable.concat(remotePostList.map { post ->
            database.insertPost(post.post)
                .andThen(database.insertReactions(post.reaction))
        })
    }

    private fun getLocalTopic(): Observable<List<PostWithReaction>> =
        database.getPostWithReaction()


    private fun getRemoteTopic(query: HashMap<String, Any>) =
        networkService.getRemotePostList(query)
            .map { response -> response.remotePostList.map { it.toLocalPostWithReaction() } }
            .toObservable()


    private fun insertTopicToDB(
        remotePostList: List<PostWithReaction>,
    ): Completable =
        database.deleteTopic()
            .andThen(insertPostList(remotePostList))

    private fun latestTopicQuery(streamName: String, topicName: String) =
        hashMapOf<String, Any>(
            "anchor" to "newest",
            "num_before" to 50,
            "num_after" to 0,
            "narrow" to Json.encodeToString(listOf(NarrowObject(streamName, "stream"),
                NarrowObject(topicName, "topic")))
        )

    private fun prevPageQuery(streamName: String, topicName: String, anchorId: Int) =
            hashMapOf<String, Any>(
                "anchor" to anchorId,
                "num_before" to 20,
                "num_after" to 0,
                "narrow" to Json.encodeToString(listOf(NarrowObject(streamName, "stream"),
                    NarrowObject(topicName, "topic")))
            )

    private fun nextPageQuery(streamName: String, topicName: String, anchorId: Int) =
            hashMapOf<String, Any>(
                "anchor" to anchorId,
                "num_before" to 0,
                "num_after" to 20,
                "narrow" to Json.encodeToString(listOf(NarrowObject(streamName, "stream"),
                    NarrowObject(topicName, "topic")))
            )


    @Serializable
    private data class NarrowObject(
        val operand: String,
        val operator: String,
    )

}