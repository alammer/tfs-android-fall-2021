package com.example.tfs.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tfs.database.dao.ContactDataDao
import com.example.tfs.database.dao.StreamDataDao
import com.example.tfs.database.dao.TopicDataDao
import com.example.tfs.database.entity.*

@Database(
    entities = [LocalStream::class, LocalPost::class, LocalReaction::class, LocalUser::class, LocalOwner::class],
    version = 1,
    exportSchema = false
)
abstract class MessengerDB : RoomDatabase() {

    abstract val streamDataDao: StreamDataDao
    abstract val topicDataDao: TopicDataDao
    abstract val contactDataDao: ContactDataDao
}