package com.example.tfs.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tfs.MessengerApp
import com.example.tfs.database.entity.*

@Database(entities = [LocalStream::class, PostReactionXRef::class, LocalPost::class, LocalReaction::class, LocalUser::class], version = 1, exportSchema = false)
abstract class MessengerDB : RoomDatabase(){

    abstract val localDataDao: MessengerDataDao

    companion object {
        private const val DB_NAME = "Zulip.db"
        val instance: MessengerDB  by lazy {
            Room.databaseBuilder(MessengerApp.appContext, MessengerDB::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}