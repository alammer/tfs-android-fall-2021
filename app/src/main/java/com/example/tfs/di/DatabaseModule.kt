package com.example.tfs.di

import android.content.Context
import androidx.room.Room
import com.example.tfs.database.MessengerDB
import com.example.tfs.database.MessengerDataDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideMessengerDatabase(context: Context): MessengerDB {
        return Room.databaseBuilder(context, MessengerDB::class.java,
            DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUsersDao(database: MessengerDB): MessengerDataDao = database.localDataDao

}

private const val DB_NAME = "Zulip.db"