package com.example.tfs.domain.topic

import android.util.Log
import com.example.tfs.database.dao.TopicDataDao
import com.example.tfs.database.entity.LocalPost
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.toLocalPostWithReaction
import com.example.tfs.util.retryWhenError
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface TopicRepository {

    fun fetchTopic(streamName: String, topicName: String): Observable<List<PostWithReaction>>

    fun fetchNextPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Single<List<PostWithReaction>>

    fun fetchPrevPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Single<List<PostWithReaction>>

    fun sendMessage(
        streamName: String,
        topicName: String,
        content: String,
    ): Single<List<PostWithReaction>>

    fun updateReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>>
}

@OptIn(ExperimentalSerializationApi::class)
class TopicRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: TopicDataDao,
    private val ownerId: Int
) : TopicRepository {


    override fun fetchTopic(
        streamName: String,
        topicName: String,
    ): Observable<List<PostWithReaction>> {
        val remoteSource: Observable<List<PostWithReaction>> =
            getRemoteTopic(newestTopicQuery(streamName, topicName))
                .toObservable()
                .retryWhenError(3, 1)

        return getLocalTopic()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localPostList: List<PostWithReaction> ->
                remoteSource
                    .flatMapSingle { remotePostList ->
                        localDao.deleteDraftPosts()
                            .andThen(insertTopicToDB(remotePostList))
                            .andThen(Single.just(remotePostList))
                    }
                    .startWith(localPostList)
            }
    }

    override fun fetchNextPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Single<List<PostWithReaction>> =
        fetchPage(nextPageQuery(streamName, topicName, anchorId))

    override fun fetchPrevPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Single<List<PostWithReaction>> =
        fetchPage(prevPageQuery(streamName, topicName, anchorId), isNext = false)

    override fun sendMessage(
        streamName: String,
        topicName: String,
        content: String,
    ): Single<List<PostWithReaction>> =
        remoteApi.sendMessage(streamName, topicName, content)
            .subscribeOn(Schedulers.io())
            .andThen(
                localDao.insertPost(
                    LocalPost(
                        postId = -(System.currentTimeMillis() * 1000).toInt() % 10000,
                        topicName = topicName,
                        streamName = streamName,
                        isSelf = true,
                        senderId = ownerId,
                        timeStamp = System.currentTimeMillis() * 1000L,
                        content = content
                    )
                )
            )
            .andThen(getLocalTopic())

    override fun updateReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>> {
        return localDao.getReactionForPost(messageId, emojiCode, ownerId)
            .defaultIfEmpty(emptyReaction)
            .flatMapSingle { reaction ->
                if (reaction.userId == -1) {
                    addReaction(messageId, emojiName, emojiCode)
                } else {
                    removeReaction(messageId, emojiName, emojiCode)
                }
            }
    }

    private fun getLocalTopic(): Single<List<PostWithReaction>> =
        localDao.getPostWithReaction()


    private fun getRemoteTopic(query: HashMap<String, Any>) =
        remoteApi.getRemotePostList(query)
            .map { response -> response.remotePostList.map { it.toLocalPostWithReaction(ownerId) } }


    private fun insertTopicToDB(
        remotePostList: List<PostWithReaction>,
    ): Completable =
        localDao.deleteTopic()
            .andThen(insertPostList(remotePostList))

    private fun insertPostList(remotePostList: List<PostWithReaction>): Completable {
        return Completable.concat(remotePostList.map { post ->
            localDao.insertPost(post.post)
                .andThen(localDao.insertReactions(post.reaction))
        })
    }

    private fun fetchPage(
        query: HashMap<String, Any>,
        isNext: Boolean = true,
    ): Single<List<PostWithReaction>> {
        return Single.zip(getRemoteTopic(query), localDao.getTopicSize(),
            { newPage, currentSize -> Pair(newPage, currentSize) })
            .subscribeOn(Schedulers.io())
            .flatMap { (page, size) ->
                addPage(page, size, isNext)
                    .andThen(localDao.getPostWithReaction())
            }
    }
    /*.subscribeOn(Schedulers.io())    //WTF???
     .map { (page, size) -> addNextPage(query, page, size) }
     .flatMapSingle { localDao.getPostWithReaction(query.streamName, query.topicName) }*/

    private fun addPage(
        newPage: List<PostWithReaction>,
        currentSize: Int,
        isNext: Boolean = true,
    ): Completable {
        return if (newPage.size + currentSize <= 51) {
            insertPostList(newPage)
        } else {
            if (isNext) {
                localDao.removeFirstPage(newPage.size - (51 - currentSize))
                    .andThen(insertPostList(newPage))
            } else {
                localDao.removeLastPage(newPage.size - (51 - currentSize))
                    .andThen(insertPostList(newPage))
            }
        }
    }

    private fun addReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>> =
        remoteApi.addReaction(messageId, emojiName, emojiCode)
            .retry(1)
            .subscribeOn(Schedulers.io())
            .andThen(
                localDao.insertReaction(
                    LocalReaction(
                        postId = messageId,
                        name = emojiName,
                        code = emojiCode,
                        userId = ownerId,
                        isClicked = true
                    )
                )
            )
            .andThen(getLocalTopic())


    private fun removeReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>> =
        remoteApi.removeReaction(messageId, emojiName, emojiCode)
            .retry(1)
            .subscribeOn(Schedulers.io())
            .andThen(localDao.deleteReaction(messageId, emojiCode, ownerId))
            .andThen(getLocalTopic())

    private fun newestTopicQuery(streamName: String, topicName: String) =
        hashMapOf<String, Any>(
            "anchor" to "newest",
            "num_before" to 50,
            "num_after" to 0,
            "narrow" to Json.encodeToString(
                listOf(
                    NarrowObject(streamName, "stream"),
                    NarrowObject(topicName, "topic")
                )
            )
        )

    private fun prevPageQuery(streamName: String, topicName: String, anchorId: Int) =
        hashMapOf<String, Any>(
            "anchor" to anchorId,
            "num_before" to 20,
            "num_after" to 0,
            "narrow" to Json.encodeToString(
                listOf(
                    NarrowObject(streamName, "stream"),
                    NarrowObject(topicName, "topic")
                )
            )
        )

    private fun nextPageQuery(streamName: String, topicName: String, anchorId: Int) =
        hashMapOf<String, Any>(
            "anchor" to anchorId,
            "num_before" to 0,
            "num_after" to 20,
            "narrow" to Json.encodeToString(
                listOf(
                    NarrowObject(streamName, "stream"),
                    NarrowObject(topicName, "topic")
                )
            )
        )

    @Serializable
    private data class NarrowObject(
        val operand: String,
        val operator: String,
    )
}

private val emptyReaction =
    LocalReaction(postId = -1, code = "", isClicked = false, name = "", userId = -1)
