package com.example.tfs.di.core

import com.example.tfs.network.ApiService
import com.example.tfs.network.utils.addJsonConverter
import com.example.tfs.network.utils.setClient
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

@Module
class NetworkModule {

    @Provides
    @Reusable
    internal fun provideRetorfitInterface(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .setClient()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addJsonConverter()
        .build()

    @Provides
    @Reusable
    internal fun zulipApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

}

const val BASE_URL = "https://tinkoff-android-fall21.zulipchat.com/"