package com.example.tfs.network

import com.example.tfs.network.models.MessageQueueResponse
import com.example.tfs.network.models.RawStreamResponse
import com.example.tfs.network.models.SubscribedStreamResponse
import com.example.tfs.network.models.TopicResponse
import com.example.tfs.network.utils.NetworkConstants.BASE_URL
import com.example.tfs.network.utils.addJsonConverter
import com.example.tfs.network.utils.setClient
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap


interface ApiService {

    @GET("api/v1/streams")
    fun getRawStreams(): Single<RawStreamResponse>

    @GET("api/v1/users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamResponse>

    @GET("api/v1/users/me/{stream_id}/topics")
    fun getStreamRelatedTopicList(@Path("stream_id") stream_id: Int): Observable<TopicResponse>

    @GET("api/v1/messages")
    fun getTopicMessageQueue(@QueryMap options: HashMap<String, Any>): Single<MessageQueueResponse>


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