package com.example.tfs.di.core

import dagger.Module

@Module(includes = [NetworkModule::class, DatabaseModule::class])
class AppModule {
}