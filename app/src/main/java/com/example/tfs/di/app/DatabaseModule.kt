package com.example.tfs.di.app

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
    internal fun provideMessengerDatabase(context: Context): MessengerDB {
        return Room.databaseBuilder(context, MessengerDB::class.java,
            DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    internal fun provideStreamDao(database: MessengerDB): StreamDataDao = database.streamDataDao
    @Provides
    internal fun provideTopicDao(database: MessengerDB): TopicDataDao = database.topicDataDao
    @Provides
    internal fun provideContactDao(database: MessengerDB): ContactDataDao = database.contactDataDao

}

private const val DB_NAME = "Zulip.db"