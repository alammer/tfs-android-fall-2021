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

    @Query("SELECT * FROM remoteStreams WHERE is_subscribed")
    fun getSubscribedStreams(): Single<List<LocalStream>>

    @Query("SELECT * FROM remoteStreams WHERE is_subscribed")
    fun observeSubscribedStreams(): Observable<List<LocalStream>>

    @Query("SELECT * FROM remoteStreams")
    fun getAllStreams(): Single<List<LocalStream>>

    @Query("SELECT * FROM remoteStreams WHERE is_subscribed=0")
    fun observeAllStreams(): Observable<List<LocalStream>>

    @Query("SELECT * FROM remoteStreams WHERE stream_id = :streamId")
    fun getStream(streamId: Int): Maybe<LocalStream>

    @Query("DELETE FROM remoteStreams")
    fun clearStreams(): Completable
}