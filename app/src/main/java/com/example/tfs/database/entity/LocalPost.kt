package com.example.tfs.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull

@Entity(tableName = "posts")
data class LocalPost(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name="post_id")
    val postId: Int,

    @NotNull
    @ColumnInfo(name = "topic_name")
    val topicName: String,

    @NotNull
    @ColumnInfo(name = "stream_name")
    val streamName: String,

    @NotNull
    @ColumnInfo(name = "sender_id")
    val senderId: Int,

    @NotNull
    @ColumnInfo(name = "sender_name")
    val senderName: String,

    @NotNull
    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @NotNull
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "flags")
    val postFlags: List<String>,

 /*   @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "reactions")
    val reaction: List<Int>,*/
)
