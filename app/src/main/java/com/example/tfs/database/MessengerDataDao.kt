package com.example.tfs.database

import androidx.room.*
import com.example.tfs.database.entity.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessengerDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStreams(localStreams: List<LocalStream>): Completable

    @Query("SELECT * FROM streams WHERE is_subscribed = :isSubscribed")
    fun getStreams(isSubscribed: Boolean): Single<List<LocalStream>>

    @Query("DELETE FROM streams")
    fun clearStreams(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(localUsers: List<LocalUser>): Completable

    @Query("SELECT * FROM contacts")
    fun getAllUsers(): Single<List<LocalUser>>

    @Query("DELETE FROM contacts")
    fun clearContacts(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosts(localPostList: List<LocalPost>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(localPost: LocalPost): Completable

    @Transaction
    @Query("SELECT * FROM posts WHERE stream_name = :streamName AND topic_name = :topicName ORDER BY post_id ASC")
    fun getPostWithReaction(streamName: String, topicName: String): Single<List<PostWithReaction>>

    @Query("DELETE FROM posts WHERE post_id = :postId")
    fun clearPost(postId: Int): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReactions(localPosts: List<LocalReaction>): Completable

    @Query("DELETE FROM reactions WHERE owner_post_id = :postId")
    fun deleteReactions(postId: Int): Completable
}