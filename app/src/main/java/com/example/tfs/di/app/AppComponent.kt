package com.example.tfs.di.app

import android.content.Context
import com.example.tfs.database.dao.StreamDataDao
import com.example.tfs.database.dao.TopicDataDao
import com.example.tfs.database.dao.ContactDataDao
import com.example.tfs.network.ApiService
import dagger.BindsInstance
import dagger.Component
import io.reactivex.subjects.PublishSubject
import javax.inject.Scope

@Component(modules = [AppModule::class])
@AppScope
interface AppComponent {

    fun localStreamSource(): StreamDataDao
    fun localTopicSource(): TopicDataDao
    fun localUserSource(): ContactDataDao
    fun remoteSource(): ApiService
    fun rxSearchBus(): PublishSubject<String>
    fun ownerId(): Int

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun ownerId(id: Int): Builder

        fun build(): AppComponent
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope