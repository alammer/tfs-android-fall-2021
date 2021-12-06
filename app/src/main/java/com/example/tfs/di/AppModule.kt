package com.example.tfs.di

import dagger.Module

@Module(includes = [NetworkModule::class, DatabaseModule::class])
class AppModule {
}