package com.example.tfs.database

import androidx.room.*
import com.example.tfs.database.entity.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
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

    @Query("SELECT * FROM contacts WHERE user_id = :userId")
    fun getUser(userId: Int): Maybe<LocalUser>

    @Query("DELETE FROM contacts")
    fun clearContacts(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOwner(owner: LocalOwner): Completable

    @Delete
    fun deleteOwner(owner: LocalOwner): Completable

    @Query("SELECT * FROM owner")
    fun getOwner(): Maybe<LocalOwner>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosts(localPostList: List<LocalPost>): Completable

    @Query("DELETE FROM posts")
    fun deleteTopic(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(localPost: LocalPost): Completable

    @Query("SELECT count(post_id) FROM posts")
    fun getTopicSize(): Single<Int>

    @Query("DELETE FROM posts WHERE post_id IN (SELECT post_id FROM posts ORDER BY post_id ASC LIMIT :newPageSize)")
    fun removeFirstPage(newPageSize: Int): Completable

    @Query("DELETE FROM posts WHERE post_id IN (SELECT post_id FROM posts ORDER BY post_id DESC LIMIT :newPageSize)")
    fun removeLastPage(newPageSize: Int): Completable

    @Transaction
    @Query("SELECT * FROM posts")
    fun getPostWithReaction(): Single<List<PostWithReaction>>

    @Query("DELETE FROM posts WHERE post_id = :postId")
    fun deletePost(postId: Int): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReactions(localReaction: List<LocalReaction>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReaction(localReaction: LocalReaction): Completable

    @Query("SELECT * FROM reactions WHERE owner_post_id = :postId AND emoji_code = :code AND user_id = :ownerId ")
    fun getReactionForPost(postId: Int, code: String, ownerId: Int): Maybe<LocalReaction>

    @Query("DELETE FROM reactions WHERE owner_post_id = :postId AND emoji_code = :code AND user_id = :ownerId ")
    fun deleteReaction(postId: Int, code: String, ownerId: Int): Completable

    @Query("DELETE FROM reactions WHERE owner_post_id = :postId")
    fun deleteReactions(postId: Int): Completable
}