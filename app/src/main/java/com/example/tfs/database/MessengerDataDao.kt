package com.example.tfs.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tfs.database.entity.*

@Dao
interface MessengerDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllStream(localStreams: List<LocalStream>)

    @Query("SELECT * FROM streams")
    fun getAllStreams(): List<LocalStream>

    @Query("DELETE FROM streams")
    fun clearStreams()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(localUsers: List<LocalUser>)

    @Query("SELECT * FROM contacts")
    fun getAllUsers(): List<LocalUser>

    @Query("DELETE FROM contacts")
    fun clearContacts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTopics(localTopics: List<LocalTopic>)

    @Query("SELECT * FROM topics WHERE parent_stream = :parentStream ")
    fun getRelatedTopics(parentStream: String): List<LocalTopic>

    @Query("DELETE FROM topics")
    fun clearTopic()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosts(localPosts: List<LocalPost>)

    @Query("SELECT * FROM posts WHERE stream_name = :parentStream AND topic_name = :topicName ORDER BY timestamp ASC")
    fun getRelatedPosts(parentStream: String, topicName: String): List<LocalPost>

    @Query("DELETE FROM posts WHERE post_id = :postId")
    fun clearPosts(postId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReactions(localPosts: List<LocalReaction>)

    @Query("SELECT * FROM reactions WHERE post_id = :postId")
    fun getPostReactions(postId: Int): List<LocalReaction>

    @Query("DELETE FROM reactions WHERE post_id = :postId")
    fun deleteReactions(postId: Int)


}