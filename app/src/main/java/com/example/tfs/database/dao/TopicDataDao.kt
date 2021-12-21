package com.example.tfs.database.dao

import androidx.room.*
import com.example.tfs.common.baseadapter.AdapterItem
import com.example.tfs.database.entity.LocalPost
import com.example.tfs.database.entity.LocalReaction
import com.example.tfs.database.entity.LocalStream
import com.example.tfs.database.entity.PostWithReaction
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface TopicDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosts(localPostList: List<LocalPost>): Completable

    @Query("DELETE FROM posts")
    fun deleteTopic(): Completable

    @Query("SELECT count(post_id) FROM posts WHERE post_id > 0")
    fun getTopicSize(): Single<Int>

    @Query("DELETE FROM posts WHERE post_id IN (SELECT post_id FROM posts ORDER BY post_id ASC LIMIT :newPageSize)")
    fun removeFirstPage(newPageSize: Int): Completable

    @Query("DELETE FROM posts WHERE post_id IN (SELECT post_id FROM posts ORDER BY post_id DESC LIMIT :newPageSize)")
    fun removeLastPage(newPageSize: Int): Completable

    @Transaction
    @Query("SELECT * FROM posts WHERE stream_name = :streamName AND topic_name = :topicName ORDER BY post_id ASC")
    fun fetchTopicFromLocal(streamName: String, topicName: String): Single<List<PostWithReaction>>

    @Transaction
    @Query("SELECT * FROM posts ORDER BY post_id ASC")
    fun getCurrentLocalTopic(): Single<List<PostWithReaction>>

    @Query("SELECT * FROM posts WHERE post_id = :postId")
    fun getPost(postId: Int): Maybe<LocalPost>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(localPost: LocalPost): Completable

    @Query("DELETE FROM posts WHERE post_id = :postId")
    fun deletePost(postId: Int): Completable

    @Query("DELETE FROM posts WHERE post_id < 1")
    fun deleteDraftPosts(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReactions(localReaction: List<LocalReaction>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReaction(localReaction: LocalReaction): Completable

    @Query("SELECT * FROM reactions WHERE owner_post_id = :postId AND emoji_code = :code AND user_id = :ownerId ")
    fun getReactionForPost(postId: Int, code: String, ownerId: Int): Maybe<LocalReaction>

    @Query("DELETE FROM reactions WHERE owner_post_id = :postId AND emoji_code = :code AND user_id = :ownerId ")
    fun deleteReaction(postId: Int, code: String, ownerId: Int): Completable

    @Query("SELECT topics FROM streams WHERE stream_id = :streamId")
    fun getAvailableTopics(streamId: Int): Single<List<String>>
}