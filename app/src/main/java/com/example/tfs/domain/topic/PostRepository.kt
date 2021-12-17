package com.example.tfs.domain.topic

import com.example.tfs.database.dao.TopicDataDao
import com.example.tfs.database.entity.LocalPost
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.PostWithReaction
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.toLocalPostWithReaction
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface PostRepository {

    fun fetchLocalTopic(streamName: String, topicName: String): Single<List<PostWithReaction>>

    fun getRemoteTopic(streamName: String, topicName: String): Single<List<PostWithReaction>>

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
        streamName: String,
        topicName: String,
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>>
}

@OptIn(ExperimentalSerializationApi::class)
class PostRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: TopicDataDao,
    private val ownerId: Int
) : PostRepository {


    override fun fetchLocalTopic(
        streamName: String,
        topicName: String,
    ): Single<List<PostWithReaction>> = getLocalTopic(streamName, topicName)
        .subscribeOn(Schedulers.io())

    override fun getRemoteTopic(
        streamName: String,
        topicName: String
    ): Single<List<PostWithReaction>> {
        return getRemoteTopic(recentPageQuery(streamName, topicName))
            .subscribeOn(Schedulers.io())
            .flatMap { remotePostList ->
                localDao.deleteDraftPosts()
                    .andThen(insertTopicToLocal(remotePostList))
                    .andThen(Single.just(remotePostList))
            }
    }

    override fun fetchNextPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Single<List<PostWithReaction>> =
        fetchPage(streamName, topicName, anchorId)
            .subscribeOn(Schedulers.io())

    override fun fetchPrevPage(
        streamName: String,
        topicName: String,
        anchorId: Int,
    ): Single<List<PostWithReaction>> =
        fetchPage(streamName, topicName, anchorId, isNext = false)
            .subscribeOn(Schedulers.io())

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
            .andThen(getLocalTopic(streamName, topicName))

    override fun updateReaction(
        streamName: String,
        topicName: String,
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>> {
        return localDao.getReactionForPost(messageId, emojiCode, ownerId)
            .subscribeOn(Schedulers.io())
            .defaultIfEmpty(emptyReaction)
            .flatMapSingle { reaction ->
                if (reaction.userId == -1) {
                    addReaction(streamName, topicName, messageId, emojiName, emojiCode)
                } else {
                    removeReaction(streamName, topicName, messageId, emojiName, emojiCode)
                }
            }
    }

    private fun getLocalTopic(
        streamName: String,
        topicName: String
    ): Single<List<PostWithReaction>> =
        localDao.getPostWithReaction(streamName, topicName)


    private fun getRemoteTopic(query: HashMap<String, Any>) =
        remoteApi.getRemotePostList(query)
            .map { response -> response.remotePostList.map { it.toLocalPostWithReaction(ownerId) } }


    private fun insertTopicToLocal(
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
        streamName: String,
        topicName: String,
        anchorId: Int,
        isNext: Boolean = true,
    ): Single<List<PostWithReaction>> {
        val query = if (isNext) {
            nextPageQuery(streamName, topicName, anchorId)
        } else {
            prevPageQuery(streamName, topicName, anchorId)
        }
        return Single.zip(getRemoteTopic(query), localDao.getTopicSize(),
            { newPage, currentSize -> Pair(newPage, currentSize) })
            .flatMap { (page, size) ->
                addPage(page, size, isNext)
                    .andThen(localDao.getPostWithReaction(streamName, topicName))
            }
    }

    private fun addPage(
        newPage: List<PostWithReaction>,
        currentSize: Int,
        isNext: Boolean = true,
    ): Completable {
        return if (newPage.size + currentSize <= TOPIC_CACHED_SIZE + 1) {
            insertPostList(newPage)
        } else {
            if (isNext) {
                localDao.removeFirstPage(newPage.size - (TOPIC_CACHED_SIZE - currentSize + 1))
                    .andThen(insertPostList(newPage))
            } else {
                localDao.removeLastPage(newPage.size - (TOPIC_CACHED_SIZE - currentSize + 1))
                    .andThen(insertPostList(newPage))
            }
        }
    }

    private fun addReaction(
        streamName: String,
        topicName: String,
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>> =
        remoteApi.addReaction(messageId, emojiName, emojiCode)
            .retry(1)
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
            .andThen(getLocalTopic(streamName, topicName))


    private fun removeReaction(
        streamName: String,
        topicName: String,
        messageId: Int,
        emojiName: String,
        emojiCode: String,
    ): Single<List<PostWithReaction>> =
        remoteApi.removeReaction(messageId, emojiName, emojiCode)
            .retry(1)
            .andThen(localDao.deleteReaction(messageId, emojiCode, ownerId))
            .andThen(getLocalTopic(streamName, topicName))

    private fun recentPageQuery(streamName: String, topicName: String) =
        hashMapOf<String, Any>(
            "anchor" to "newest",
            "num_before" to TOPIC_CACHED_SIZE,
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
            "num_before" to PAGE_FETCH_SIZE,
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
            "num_after" to PAGE_FETCH_SIZE,
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

private const val TOPIC_CACHED_SIZE = 50
private const val PAGE_FETCH_SIZE = 20
