package com.example.tfs.network

import com.example.tfs.network.models.*
import com.example.tfs.network.utils.NetworkConstants.BASE_URL
import com.example.tfs.network.utils.addJsonConverter
import com.example.tfs.network.utils.setClient
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.*


interface ApiService {

    @GET("api/v1/streams")
    fun getRawStreams(): Single<RawStreamResponse>

    @GET("api/v1/users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamResponse>

    @GET("api/v1/users/me/{stream_id}/topics")
    fun getStreamRelatedTopicList(@Path("stream_id") stream_id: Int): Observable<TopicResponse>

    @GET("api/v1/messages")
    fun getTopicMessageQueue(@QueryMap options: HashMap<String, Any>): Single<MessageQueueResponse>

    @GET("api/v1/users")
    fun getAllUsers(): Single<UsersResponse>

    @POST("api/v1/messages")
    fun sendMessage(
        @Query("to") streamName: String,
        @Query("topic") topicName: String,
        @Query("content") reaction_type: String,
        @Query("type") type: String = "stream",
    ): Completable

    @POST("api/v1/messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") message_id: Int,
        @Query("emoji_name") emoji_name: String,
        @Query("emoji_code") emoji_code: String,
        @Query("reaction_type") reaction_type: String = "unicode_emoji"
    ): Completable

    @DELETE("api/v1/messages/{message_id}/reactions")
    fun removeReaction(
        @Path("message_id") message_id: Int,
        @Query("emoji_name") emoji_name: String,
        @Query("emoji_code") emoji_code: String,
        @Query("reaction_type") reaction_type: String = "unicode_emoji"
    ): Completable

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