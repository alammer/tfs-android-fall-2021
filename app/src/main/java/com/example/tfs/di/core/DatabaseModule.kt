package com.example.tfs.di.core

import android.content.Context
import androidx.room.Room
import com.example.tfs.database.*
import com.example.tfs.database.dao.ContactDataDao
import com.example.tfs.database.dao.StreamDataDao
import com.example.tfs.database.dao.TopicDataDao
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @AppScope
    fun provideMessengerDatabase(context: Context): MessengerDB {
        return Room.databaseBuilder(context, MessengerDB::class.java,
            DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideStreamDao(database: MessengerDB): StreamDataDao = database.streamDataDao
    @Provides
    fun provideTopicDao(database: MessengerDB): TopicDataDao = database.topicDataDao
    @Provides
    fun provideContactDao(database: MessengerDB): ContactDataDao = database.contactDataDao

}

private const val DB_NAME = "Zulip.db"