package com.example.tfs.network.models

import android.util.Log
import com.example.tfs.domain.streams.StreamItemList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamResponse(
    @SerialName("msg")
    val message: String,
    @SerialName("result")
    val result: String,
//    @SerialName("code")
//    val code: String?,
    @SerialName("streams")
    val streamsResponseList: List<Stream>?,
)

@Serializable
data class Stream(
    @SerialName("stream_id")
    val stream_id: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class TopicResponse(
    @SerialName("msg")
    val message: String,
    @SerialName("result")
    val result: String,
    @SerialName("code")
    val code: String?,
    @SerialName("topics")
    val streamsResponseList: List<Topic>?,
)

@Serializable
data class Topic(
    @SerialName("max_id")
    val max_id: Int,
    @SerialName("name")
    val name: String,
)

fun StreamResponse.toDomainStream(): List<StreamItemList> {
    val domainStreamList: MutableList<StreamItemList.StreamItem> = mutableListOf()
    streamsResponseList?.let {
        it.forEach { stream ->
            domainStreamList.add(
                StreamItemList.StreamItem(stream.stream_id, stream.name, false)
            )
        }
    }
    return domainStreamList.toList()
}