package com.example.tfs.di

import com.example.tfs.domain.streams.FetchStreams
import com.example.tfs.domain.streams.StreamRepositoryImpl
import com.example.tfs.ui.streams.elm.StreamActor
import com.example.tfs.ui.streams.elm.StreamStoreFactory

class AppDI private constructor() {

    val repository by lazy { StreamRepositoryImpl() }

    val fetchStreams by lazy { FetchStreams(repository) }

    val actor by lazy { StreamActor(fetchStreams) }

    val elmStoreFactory by lazy { StreamStoreFactory(actor) }

    companion object {

        lateinit var INSTANCE: AppDI

        fun init() {
            INSTANCE = AppDI()
        }
    }
}