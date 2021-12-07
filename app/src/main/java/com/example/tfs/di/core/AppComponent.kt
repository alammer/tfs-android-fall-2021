package com.example.tfs.di.core

import android.content.Context
import com.example.tfs.database.MessengerDataDao
import com.example.tfs.network.ApiService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

@Component(modules = [AppModule::class])
@AppScope
interface AppComponent {

    fun localSource(): MessengerDataDao
    fun remoteSource(): ApiService

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope