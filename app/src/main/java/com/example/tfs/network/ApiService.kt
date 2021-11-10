package com.example.tfs.network

import com.example.tfs.network.models.RawStreamResponse
import com.example.tfs.network.models.SubscribedStreamResponse
import com.example.tfs.network.models.TopicResponse
import com.example.tfs.network.utils.NetworkConstants.BASE_URL
import com.example.tfs.network.utils.addJsonConverter
import com.example.tfs.network.utils.setClient
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {

    @GET("streams")
    fun getStreams(): Single<RawStreamResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") stream_id: Int): Single<TopicResponse>

    companion object {

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .setClient()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addJsonConverter()
                .build()
                .create(ApiService::class.java)
        }
    }
}