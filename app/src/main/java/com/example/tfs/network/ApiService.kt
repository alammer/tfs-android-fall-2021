package com.example.tfs.network

import com.example.tfs.network.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*


interface ApiService {

    @GET("api/v1/streams")
    fun getAllStreams(
        //@Query("include_subscribed") include_subs: Boolean = false,  //don't support by backend?
    ): Single<AllStreamResponse>

    @GET("api/v1/users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamResponse>

    @GET("api/v1/users/me/{stream_id}/topics")
    fun getStreamRelatedTopicList(@Path("stream_id") stream_id: Int): Observable<TopicResponse>

    @GET("api/v1/messages")
    fun getRemotePostList(@QueryMap options: HashMap<String, Any>): Single<PostListResponse>

    @GET("api/v1/users")
    fun getAllUsers(): Single<UserListResponse>

    @GET("api/v1/users/me")
    fun getOwner(): Single<User>

    @GET("api/v1/users/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Single<UserResponse>

    @GET("api/v1/users/{user_id}/presence")
    fun getUserPresence(@Path("user_id") userId: Int): Single<UserPresence>

    @POST("api/v1/messages")
    fun sendMessage(
        @Query("to") streamName: String,
        @Query("topic") topicName: String,
        @Query("content") content: String,
        @Query("type") type: String = "stream",
        //@Query("apply_markdown") markdown: Boolean = true
    ): Completable

    @DELETE("api/v1/messages/{message_id}")
    fun deleteMessage(
        @Path("message_id") message_id: Int,
    ): Completable

    @PATCH("api/v1/messages/{message_id}")
    fun editMessage(
        @Path("message_id") message_id: Int,
        @Query("content") content: String,
    ): Completable


    @POST("api/v1/messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") message_id: Int,
        @Query("emoji_name") emoji_name: String,
        @Query("emoji_code") emoji_code: String,
        @Query("reaction_type") reaction_type: String = "unicode_emoji",
    ): Completable

    @DELETE("api/v1/messages/{message_id}/reactions")
    fun removeReaction(
        @Path("message_id") message_id: Int,
        @Query("emoji_name") emoji_name: String,
        @Query("emoji_code") emoji_code: String,
        @Query("reaction_type") reaction_type: String = "unicode_emoji",
    ): Completable
}