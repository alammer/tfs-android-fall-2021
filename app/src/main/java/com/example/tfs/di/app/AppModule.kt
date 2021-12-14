package com.example.tfs.di.app

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject

@Module(includes = [NetworkModule::class, DatabaseModule::class])
class AppModule {

    @AppScope
    @Provides
    fun provideRxSearchBus(): PublishSubject<String> = PublishSubject.create()
}