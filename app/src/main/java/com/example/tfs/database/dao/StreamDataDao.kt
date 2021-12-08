package com.example.tfs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tfs.database.entity.LocalStream
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface StreamDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertStreams(localStreams: List<LocalStream>): Completable

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertStream(localStream: LocalStream): Completable

        @Query("SELECT * FROM streams WHERE is_subscribed = 1")
        fun getSubscribedStreams(): Observable<List<LocalStream>>

        @Query("SELECT * FROM streams")
        fun getAllStreams(): Observable<List<LocalStream>>

        @Query("SELECT * FROM streams WHERE stream_id = :streamId")
        fun getStream(streamId: Int): Maybe<LocalStream>

        @Query("DELETE FROM streams")
        fun clearStreams(): Completable
}