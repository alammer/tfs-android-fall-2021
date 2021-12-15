package com.example.tfs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tfs.database.entity.LocalStream
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface StreamDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStreams(localStreams: List<LocalStream>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStream(localStream: LocalStream): Completable

    @Query("SELECT * FROM streams WHERE is_subscribed ORDER BY stream_name COLLATE NOCASE")
    fun getSubscribedStreams(): Single<List<LocalStream>>

    /*@Query("SELECT * FROM streams WHERE is_subscribed")
    fun observeSubscribedStreams(): Observable<List<LocalStream>>*/

    @Query("SELECT * FROM streams")
    fun getAllStreams(): Single<List<LocalStream>>

    @Query("SELECT * FROM streams WHERE is_subscribed=0 ORDER BY stream_name COLLATE NOCASE")
    fun getUnsubscribedStreams(): Single<List<LocalStream>>

    /*@Query("SELECT * FROM streams WHERE is_subscribed=0")
    fun observeAllStreams(): Observable<List<LocalStream>>*/

    @Query("SELECT * FROM streams WHERE stream_id = :streamId")
    fun getStream(streamId: Int): Maybe<LocalStream>

    @Query("DELETE FROM streams WHERE is_subscribed=0")
    fun clearUnsubscribedStreams(): Completable

    @Query("DELETE FROM streams WHERE is_subscribed")
    fun clearSubscribedStreams(): Completable
}